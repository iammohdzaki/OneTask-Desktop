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
val view_column: ImageVector
  get() {
    if (_view_column != null) {
      return _view_column!!
    }
    _view_column =
      ImageVector.Builder(
          name = "view_column",
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
            moveTo(3.03f, 17f)
            verticalLineTo(7f)
            quadToRelative(0f, -0.82f, 0.59f, -1.41f)
            reflectiveQuadTo(5.03f, 5f)
            horizontalLineTo(19f)
            quadToRelative(0.83f, 0f, 1.41f, 0.59f)
            quadTo(21f, 6.18f, 21f, 7f)
            verticalLineTo(17f)
            quadToRelative(0f, 0.82f, -0.59f, 1.41f)
            reflectiveQuadTo(19f, 19f)
            horizontalLineTo(5.03f)
            quadTo(4.2f, 19f, 3.61f, 18.41f)
            reflectiveQuadTo(3.03f, 17f)
            close()
            moveTo(5f, 17f)
            horizontalLineTo(8.33f)
            verticalLineTo(7f)
            horizontalLineTo(5f)
            verticalLineTo(17f)
            close()
            moveToRelative(5.33f, 0f)
            horizontalLineToRelative(3.32f)
            verticalLineTo(7f)
            horizontalLineTo(10.33f)
            verticalLineTo(17f)
            close()
            moveToRelative(5.32f, 0f)
            horizontalLineToRelative(3.33f)
            verticalLineTo(7f)
            horizontalLineTo(15.65f)
            verticalLineTo(17f)
            close()
          }
        }
        .build()
    return _view_column!!
  }

private var _view_column: ImageVector? = null
