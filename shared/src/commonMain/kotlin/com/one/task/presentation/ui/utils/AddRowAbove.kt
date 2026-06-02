package com.one.task.presentation.ui.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("CheckReturnValue")
val add_row_above: ImageVector
  get() {
    if (_add_row_above != null) {
      return _add_row_above!!
    }
    _add_row_above =
      ImageVector.Builder(
          name = "add_row_above",
          defaultWidth = 24.dp,
          defaultHeight = 24.dp,
          viewportWidth = 24f,
          viewportHeight = 24f,
        )
        .apply {
          path(
            fill = SolidColor(Color.Black),
            fillAlpha = 1f,
            stroke = null,
            strokeAlpha = 1f,
            strokeLineWidth = 1f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Bevel,
            strokeLineMiter = 1f,
            pathFillType = PathFillType.NonZero,
          ) {
            moveTo(5f, 20f)
            horizontalLineTo(19f)
            verticalLineTo(14f)
            horizontalLineTo(5f)
            verticalLineToRelative(6f)
            close()
            moveToRelative(16f, 2f)
            horizontalLineTo(3f)
            verticalLineTo(4f)
            horizontalLineTo(7f)
            quadTo(7f, 4.25f, 7f, 4.49f)
            reflectiveQuadTo(7f, 5f)
            quadTo(7f, 5.27f, 7f, 5.51f)
            reflectiveQuadTo(7f, 6f)
            horizontalLineTo(5f)
            verticalLineToRelative(6f)
            horizontalLineTo(19f)
            verticalLineTo(6f)
            horizontalLineTo(17f)
            quadTo(17f, 5.75f, 17f, 5.51f)
            reflectiveQuadTo(17f, 5f)
            quadTo(17f, 4.72f, 17f, 4.49f)
            reflectiveQuadTo(17f, 4f)
            horizontalLineToRelative(4f)
            verticalLineTo(22f)
            close()
            moveTo(12f, 12f)
            close()
            moveToRelative(0f, 2f)
            verticalLineTo(12f)
            verticalLineToRelative(2f)
            close()
            moveToRelative(0f, 0f)
            close()
            moveTo(11f, 8f)
            verticalLineTo(6f)
            horizontalLineTo(9f)
            verticalLineTo(4f)
            horizontalLineToRelative(2f)
            verticalLineTo(2f)
            horizontalLineToRelative(2f)
            verticalLineTo(4f)
            horizontalLineToRelative(2f)
            verticalLineTo(6f)
            horizontalLineTo(13f)
            verticalLineTo(8f)
            horizontalLineTo(11f)
            close()
          }
        }
        .build()
    return _add_row_above!!
  }

private var _add_row_above: ImageVector? = null
