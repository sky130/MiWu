package com.github.miwu.service

import android.annotation.SuppressLint
import android.content.Context
import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.DeviceParametersBuilders
import androidx.wear.protolayout.DeviceParametersBuilders.*
import androidx.wear.protolayout.DimensionBuilders
import androidx.wear.protolayout.DimensionBuilders.*
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.LayoutElementBuilders.*
import androidx.wear.protolayout.ModifiersBuilders
import androidx.wear.protolayout.ModifiersBuilders.Padding
import androidx.wear.protolayout.ModifiersBuilders.*
import androidx.wear.protolayout.material.Text
import androidx.wear.protolayout.material.Button
import androidx.wear.protolayout.material.ButtonColors
import androidx.wear.protolayout.material.Chip
import androidx.wear.protolayout.material.ChipColors
import androidx.wear.protolayout.material.Colors
import androidx.wear.protolayout.material.CompactChip
import androidx.wear.tiles.TileService
import kndroidx.extension.log

fun Column(
    width: ContainerDimension,
    height: ContainerDimension,
    padding: PaddingValue? = null,
    modifiers: Modifiers.Builder.() -> Unit = {},
    block: Column.Builder.() -> Unit = {},
) = Column.Builder().apply {
    setWidth(width)
    setHeight(height)
    setModifiers(
        Modifiers.Builder().apply {
            if (padding != null) setPadding(
                Padding.Builder().apply {
                    setTop(padding.top)
                    setBottom(padding.bottom)
                    setStart(padding.start)
                    setEnd(padding.end)
                }.build()
            )
            modifiers()
        }.build()
    )
    block()
}.build()


fun Row(
    width: ContainerDimension,
    height: ContainerDimension,
    padding: PaddingValue? = null,
    modifiers: Modifiers.Builder.() -> Unit = {},
    block: Row.Builder.() -> Unit = {},
) = Row.Builder().apply {
    setWidth(width)
    setHeight(height)
    setModifiers(
        Modifiers.Builder().apply {
            if (padding != null) setPadding(
                Padding.Builder().apply {
                    setTop(padding.top)
                    setBottom(padding.bottom)
                    setStart(padding.start)
                    setEnd(padding.end)
                }.build()
            )
            modifiers()
        }.build()
    )
    block()
}.build()


fun Box(
    width: ContainerDimension,
    height: ContainerDimension,
    padding: PaddingValue? = null,
    modifiers: Modifiers.Builder.() -> Unit = {},
    block: Box.Builder.() -> Unit = {},
) = Box.Builder().apply {
    setWidth(width)
    setHeight(height)
    setModifiers(
        Modifiers.Builder().apply {
            if (padding != null) setPadding(
                Padding.Builder().apply {
                    setTop(padding.top)
                    setBottom(padding.bottom)
                    setStart(padding.start)
                    setEnd(padding.end)
                }.build()
            )
            modifiers()
        }.build()
    )
    block()
}.build()

@Suppress("FunctionName")
fun Grid(
    width: ContainerDimension,
    height: ContainerDimension,
    padding: PaddingValue? = null,
    spanCount: Int = 1,
    rowPadding: PaddingValue? = null,

    modifiers: Modifiers.Builder.() -> Unit = {},
    block: Grid.() -> Unit = {},
) = Column(width, height, padding, modifiers) {
    if (spanCount < 1) throw Exception("SpanCount cannot be $spanCount.")
    Grid(this, spanCount, rowPadding).apply(block).build()
}

class Grid(
    private val column: Column.Builder,
    private val spanCount: Int,
    private val rowPadding: PaddingValue?,
) {
    private val list = arrayListOf<LayoutElement>()

    fun build() {
        var x = 0
        var row = getRowBuilder()
        for (i in list) {
            if (x == spanCount) {
                column.contents(row.build())
                row = getRowBuilder()
                x = 0
            }
            i.log.d()
            row.contents(i)
            x++
        }
        column.contents(row.build())
    }

    private fun getRowBuilder() = Row.Builder().setHeight(wrap()).setWidth(expand()).apply {
        setModifiers(Modifiers.Builder().apply {
            setVerticalAlignment(Vertical(VERTICAL_ALIGN_CENTER))
        }.setPadding(Padding.Builder().apply {
            rowPadding?.let {
                setEnd(it.end)
                setTop(it.top)
                setBottom(it.bottom)
                setStart(it.start)
            }
        }.build()).build())
    }

    fun Grid.contents(vararg elements: LayoutElement) {
        list.addAll(elements)
    }
}

@SuppressLint("RestrictedApi")
fun Column.Builder.contents(vararg elements: LayoutElement) {
    for (i in elements) {
        addContent(i)
    }
}

@SuppressLint("RestrictedApi")
fun Row.Builder.contents(vararg elements: LayoutElement) {
    for (i in elements) {
        addContent(i)
    }
}

@SuppressLint("RestrictedApi")
fun Box.Builder.contents(vararg elements: LayoutElement) {
    for (i in elements) {
        addContent(i)
    }
}

fun TileService.Text(
    text: String,
    padding: PaddingValue? = null,
    modifiers: Modifiers.Builder.() -> Unit = {},
    block: Text.Builder.() -> Unit = {},
) = Text.Builder(this, text).apply {
    setModifiers(
        Modifiers.Builder().apply {
            if (padding != null) setPadding(
                Padding.Builder().apply {
                    setTop(padding.top)
                    setBottom(padding.bottom)
                    setStart(padding.start)
                    setEnd(padding.end)
                }.build()
            )
            modifiers()
        }.build()
    )
    block()
}.build()

fun TileService.Spacer(
    width: DpProp,
    height: DpProp,
    padding: PaddingValue? = null,
    modifiers: Modifiers.Builder.() -> Unit = {},
    block: Spacer.Builder.() -> Unit = {},
) = Spacer.Builder().apply {
    setWidth(width)
    setHeight(height)
    setModifiers(
        Modifiers.Builder().apply {
            if (padding != null) setPadding(
                Padding.Builder().apply {
                    setTop(padding.top)
                    setBottom(padding.bottom)
                    setStart(padding.start)
                    setEnd(padding.end)
                }.build()
            )
            modifiers()
        }.build()
    )
    block()
}.build()

fun TileService.Image(
    width: DpProp,
    height: DpProp,
    padding: PaddingValue? = null,
    modifiers: Modifiers.Builder.() -> Unit = {},
    resId: String = "",
    block: Image.Builder.() -> Unit = {},
) = Image.Builder().apply {
    if (resId.isNotEmpty()) {
        setResourceId(resId)
    }
    setWidth(width)
    setHeight(height)
    setModifiers(
        Modifiers.Builder().apply {
            if (padding != null) setPadding(
                Padding.Builder().apply {
                    setTop(padding.top)
                    setBottom(padding.bottom)
                    setStart(padding.start)
                    setEnd(padding.end)
                }.build()
            )
            modifiers()
        }.build()
    )
    block()
}.build()

fun TileService.Button(
    size: DpProp? = null,
    text: String? = null,
    imageResId: String? = null,
    content: LayoutElement? = null,
    colors: ButtonColors? = null,
    clickable: Clickable,
    block: Button.Builder.() -> Unit = {},
) = Button.Builder(this, clickable).apply {
    text?.let { setTextContent(it) }
    colors?.let { setButtonColors(it) }
    imageResId?.let { setImageContent(it) }
    size?.let { setSize(it) }
    content?.let { setCustomContent(it) }
    block()
}.build()

fun TileService.Chip(
    widthDp: DpProp? = null,
    width: ContainerDimension? = null,
    clickable: Clickable,
    title: String? = null,
    label: String? = null,
    icon: String? = null,
    colors: ChipColors? = null,
    content: LayoutElement? = null,
    block: Chip.Builder.() -> Unit = {},
) = Chip.Builder(this, clickable, DeviceParameters.Builder().build()).apply {
    width?.let { setWidth(it) }
    widthDp?.let { setWidth(it) }
    colors?.let { setChipColors(it) }
    title?.let { setPrimaryLabelContent(it) }
    label?.let { setSecondaryLabelContent(it) }
    icon?.let { setIconContent(it) }
    content?.let { setCustomContent(it) }
    block()
}.build()

fun TileService.CompactChip(
    clickable: Clickable,
    text: String,
    icon: String? = null,
    colors: ChipColors? = null,
    block: CompactChip.Builder.() -> Unit = {},
) = CompactChip.Builder(this, text, clickable, DeviceParameters.Builder().build()).apply {
    colors?.let { setChipColors(it) }
    icon?.let { setIconContent(it) }
    block()
}.build()


fun Clickable(id: String) = Clickable.Builder().apply {
    setId(id)
    setOnClick(ActionBuilders.LoadAction.Builder().build())
}.build()

fun Clickable(id: String, packageName: String, className: String) = Clickable.Builder().apply {
    setOnClick(ActionBuilders.LaunchAction.Builder().apply {
        setId(id)

        setAndroidActivity(ActionBuilders.AndroidActivity.Builder().apply {
            setPackageName(packageName)
            setClassName(className)
        }.build())

    }.build())

}.build()

@Suppress("FunctionName")
fun Horizontal(value: Int) = HorizontalAlignmentProp.Builder().setValue(
    value
).build()

@Suppress("FunctionName")
fun Vertical(value: Int) = VerticalAlignmentProp.Builder().setValue(
    value
).build()

fun PaddingValue(dp: DpProp) = PaddingValue(dp, dp, dp, dp)
fun PaddingValue(vertical: DpProp = 0.dp, horizontal: DpProp = 0.dp) =
    PaddingValue(vertical, vertical, horizontal, horizontal)


data class PaddingValue(val top: DpProp, val bottom: DpProp, val start: DpProp, val end: DpProp)

val Number.dp get() = dp(this.toFloat())