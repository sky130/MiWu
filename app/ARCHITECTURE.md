# App architecture

The application code is split into four layers under `com.github.miwu`:

- `domain`: Pure Kotlin models, repository contracts, gateways, and reusable use cases.
- `data`: Repository implementations, remote orchestration, Room, DataStore, and settings storage.
- `platform`: Android-specific integration such as crash handling, device identity, and Wear Tile refresh.
- `ui`: Activities, fragments, ViewModels, UI state, and Data Binding adapters.

`di` is the composition root. Koin is allowed to see every layer; feature code should not use Koin as a service locator.

## Dependency direction

```text
ui --------> domain <-------- data
                ^
                |
             platform

data and platform may use Android APIs, but they communicate through domain contracts.

di --------> all layers
```

Runtime implementations are connected in `di`, but source dependencies point toward `domain` contracts.

## Rules

1. `domain` must not import `android.*`, `androidx.*`, `data`, `platform`, or `ui`.
2. Room entities and DataStore serializers remain inside `data` and never appear in ViewModel APIs.
3. Repositories are named after business capabilities, not storage mechanisms such as `Local` or `Cache`.
4. One-shot writes are main-safe `suspend` functions. The caller owns their lifecycle.
5. Observable data is exposed as immutable `Flow` or `StateFlow`.
6. Long-lived application scopes are reserved for state synchronization and platform observers.
7. ViewModels depend on domain contracts/use cases and expose immutable state or events.
8. UI-specific state mapping remains in `ui`; network response assembly remains in `data`.

## Main ownership

- `AccountRepository` is the single source of truth for the current user and login state.
- `HomeRepository` owns home lists, the active home, scenes, and user profile data.
- `FavoriteDeviceRepository` owns the ordered favorite-device list.
- `DeviceMetadataRepository` owns model icon URLs and device-room metadata.
- `DeviceIconRepository` owns downloaded Tile icon bytes.
- `SettingsRepository` owns persisted application selections and flags.
