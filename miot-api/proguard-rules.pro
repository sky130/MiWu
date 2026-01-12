# Serializer for classes with named companion objects are retrieved using `getDeclaredClasses`.
# If you have any, replace classes with those containing named companion objects.
-keepattributes InnerClasses # Needed for `getDeclaredClasses`.

-if @kotlinx.serialization.Serializable class
com.example.myapplication.HasNamedCompanion, # <-- List serializable classes with named companions.
com.example.myapplication.HasNamedCompanion2
{
    static **$* *;
}
-keepnames class <1>$$serializer { # -keepnames suffices; class is kept when serializer() is kept.
    static <1>$$serializer INSTANCE;
}

# Keep both serializer and serializable classes to save the attribute InnerClasses
-keepclasseswithmembers, allowshrinking, allowobfuscation, allowaccessmodification class
com.example.myapplication.HasNamedCompanion, # <-- List serializable classes with named companions.
com.example.myapplication.HasNamedCompanion2
{
    *;
}