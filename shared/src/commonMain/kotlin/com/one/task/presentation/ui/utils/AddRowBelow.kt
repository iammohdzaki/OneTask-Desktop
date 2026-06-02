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
val add_row_below: ImageVector
  get() {
    if (_add_row_below != null) {
      return _add_row_below!!
    }
    _add_row_below =
      ImageVector.Builder(
          name = "add_row_below",
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
            moveTo(5f, 10f)
            horizontalLineTo(19f)
            verticalLineTo(4f)
            horizontalLineTo(5f)
            verticalLineToRelative(6f)
            close()
            moveTo(3f, 20f)
            verticalLineTo(2f)
            horizontalLineTo(21f)
            verticalLineTo(20f)
            horizontalLineTo(17f)
            quadToRelative(0f, -0.25f, 0f, -0.49f)
            reflectiveQuadTo(17f, 19f)
            reflectiveQuadToRelative(0f, -0.51f)
            reflectiveQuadTo(17f, 18f)
            horizontalLineToRelative(2f)
            verticalLineTo(12f)
            horizontalLineTo(5f)
            verticalLineToRelative(6f)
            horizontalLineTo(7f)
            quadToRelative(0f, 0.25f, 0f, 0.49f)
            reflectiveQuadTo(7f, 19f)
            reflectiveQuadToRelative(0f, 0.51f)
            reflectiveQuadTo(7f, 20f)
            horizontalLineTo(3f)
            close()
            moveToRelative(9f, -8f)
            close()
            moveToRelative(0f, -2f)
            verticalLineToRelative(2f)
            verticalLineTo(10f)
            close()
            moveToRelative(0f, 0f)
            close()
            moveTo(11f, 22f)
            verticalLineTo(20f)
            horizontalLineTo(9f)
            verticalLineTo(18f)
            horizontalLineToRelative(2f)
            verticalLineTo(16f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(2f)
            horizontalLineTo(13f)
            verticalLineToRelative(2f)
            horizontalLineTo(11f)
            close()
          }
        }
        .build()
    return _add_row_below!!
  }

private var _add_row_below: ImageVector? = null
