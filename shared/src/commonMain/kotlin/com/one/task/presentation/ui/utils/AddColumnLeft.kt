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
val add_column_left: ImageVector
  get() {
    if (_add_column_left != null) {
      return _add_column_left!!
    }
    _add_column_left =
      ImageVector.Builder(
          name = "add_column_left",
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
            moveTo(20f, 19f)
            verticalLineTo(5f)
            horizontalLineTo(14f)
            verticalLineTo(19f)
            horizontalLineToRelative(6f)
            close()
            moveTo(4f, 21f)
            verticalLineTo(17f)
            quadToRelative(0.25f, 0f, 0.49f, 0f)
            quadTo(4.73f, 17f, 5f, 17f)
            reflectiveQuadToRelative(0.51f, 0f)
            reflectiveQuadTo(6f, 17f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(6f)
            verticalLineTo(5f)
            horizontalLineTo(6f)
            verticalLineTo(7f)
            quadTo(5.75f, 7f, 5.51f, 7f)
            reflectiveQuadTo(5f, 7f)
            quadTo(4.73f, 7f, 4.49f, 7f)
            reflectiveQuadTo(4f, 7f)
            verticalLineTo(3f)
            horizontalLineTo(22f)
            verticalLineTo(21f)
            horizontalLineTo(4f)
            close()
            moveToRelative(8f, -9f)
            close()
            moveToRelative(2f, 0f)
            horizontalLineTo(12f)
            horizontalLineToRelative(2f)
            close()
            moveToRelative(0f, 0f)
            close()
            moveTo(4f, 15f)
            verticalLineTo(13f)
            horizontalLineTo(2f)
            verticalLineTo(11f)
            horizontalLineTo(4f)
            verticalLineTo(9f)
            horizontalLineTo(6f)
            verticalLineToRelative(2f)
            horizontalLineTo(8f)
            verticalLineToRelative(2f)
            horizontalLineTo(6f)
            verticalLineToRelative(2f)
            horizontalLineTo(4f)
            close()
          }
        }
        .build()
    return _add_column_left!!
  }

private var _add_column_left: ImageVector? = null
