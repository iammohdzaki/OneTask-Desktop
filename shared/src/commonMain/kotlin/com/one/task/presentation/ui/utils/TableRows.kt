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
val table_rows: ImageVector
  get() {
    if (_table_rows != null) {
      return _table_rows!!
    }
    _table_rows =
      ImageVector.Builder(
          name = "table_rows",
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
            moveTo(19f, 19f)
            verticalLineTo(16f)
            horizontalLineTo(5f)
            verticalLineToRelative(3f)
            horizontalLineTo(19f)
            close()
            moveToRelative(0f, -5f)
            verticalLineTo(10f)
            horizontalLineTo(5f)
            verticalLineToRelative(4f)
            horizontalLineTo(19f)
            close()
            moveTo(19f, 8f)
            verticalLineTo(5f)
            horizontalLineTo(5f)
            verticalLineTo(8f)
            horizontalLineTo(19f)
            close()
            moveTo(5f, 21f)
            quadTo(4.18f, 21f, 3.59f, 20.41f)
            reflectiveQuadTo(3f, 19f)
            verticalLineTo(5f)
            quadTo(3f, 4.17f, 3.59f, 3.59f)
            reflectiveQuadTo(5f, 3f)
            horizontalLineTo(19f)
            quadToRelative(0.83f, 0f, 1.41f, 0.59f)
            reflectiveQuadTo(21f, 5f)
            verticalLineTo(19f)
            quadToRelative(0f, 0.82f, -0.59f, 1.41f)
            reflectiveQuadTo(19f, 21f)
            horizontalLineTo(5f)
            close()
          }
        }
        .build()
    return _table_rows!!
  }

private var _table_rows: ImageVector? = null
