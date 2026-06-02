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
val add_column_right: ImageVector
  get() {
    if (_add_column_right != null) {
      return _add_column_right!!
    }
    _add_column_right =
      ImageVector.Builder(
          name = "add_column_right",
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
            moveTo(4f, 5f)
            verticalLineTo(19f)
            horizontalLineToRelative(6f)
            verticalLineTo(5f)
            horizontalLineTo(4f)
            close()
            moveTo(2f, 21f)
            verticalLineTo(3f)
            horizontalLineTo(20f)
            verticalLineTo(7f)
            quadTo(19.75f, 7f, 19.51f, 7f)
            reflectiveQuadTo(19f, 7f)
            quadTo(18.73f, 7f, 18.49f, 7f)
            reflectiveQuadTo(18f, 7f)
            verticalLineTo(5f)
            horizontalLineTo(12f)
            verticalLineTo(19f)
            horizontalLineToRelative(6f)
            verticalLineTo(17f)
            quadToRelative(0.25f, 0f, 0.49f, 0f)
            reflectiveQuadTo(19f, 17f)
            quadToRelative(0.28f, 0f, 0.51f, 0f)
            reflectiveQuadTo(20f, 17f)
            verticalLineToRelative(4f)
            horizontalLineTo(2f)
            close()
            moveTo(12f, 12f)
            close()
            moveToRelative(-2f, 0f)
            horizontalLineToRelative(2f)
            horizontalLineTo(10f)
            close()
            moveToRelative(0f, 0f)
            close()
            moveToRelative(8f, 3f)
            verticalLineTo(13f)
            horizontalLineTo(16f)
            verticalLineTo(11f)
            horizontalLineToRelative(2f)
            verticalLineTo(9f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(2f)
            horizontalLineTo(20f)
            verticalLineToRelative(2f)
            horizontalLineTo(18f)
            close()
          }
        }
        .build()
    return _add_column_right!!
  }

private var _add_column_right: ImageVector? = null
