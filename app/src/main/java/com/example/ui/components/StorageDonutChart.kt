package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CloudProvider
import com.example.data.model.StorageQuotaSummary
import com.example.data.repository.CloudStorageRepository

@Composable
fun StorageDonutChart(
  summary: StorageQuotaSummary,
  modifier: Modifier = Modifier
) {
  val animationProgress = remember { Animatable(0f) }

  LaunchedEffect(summary) {
    animationProgress.animateTo(
      targetValue = 1f,
      animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing)
    )
  }

  val gDriveBytes = summary.googleDriveUsedBytes
  val dropboxBytes = summary.dropboxUsedBytes
  val fixedTotal = summary.totalAllocatedBytes.coerceAtLeast(1L)

  // Percentages of total allocated space
  val gDriveSweep = ((gDriveBytes.toFloat() / fixedTotal.toFloat()) * 360f).coerceIn(0f, 360f)
  val dropboxSweep = ((dropboxBytes.toFloat() / fixedTotal.toFloat()) * 360f).coerceIn(0f, 360f)

  val emptyTrackColor = Color(0xFFE1E2E9)

  Box(
    modifier = modifier.size(190.dp),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.size(175.dp)) {
      val strokeWidth = 16.dp.toPx()
      val arcSize = size.width - strokeWidth
      val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)

      // Background Empty Track
      drawArc(
        color = emptyTrackColor,
        startAngle = 0f,
        sweepAngle = 360f,
        useCenter = false,
        topLeft = topLeft,
        size = Size(arcSize, arcSize),
        style = Stroke(width = strokeWidth)
      )

      var currentStartAngle = -90f

      // Google Drive Arc
      if (gDriveSweep > 0f) {
        val sweep = gDriveSweep * animationProgress.value
        drawArc(
          color = CloudProvider.GOOGLE_DRIVE.brandColor,
          startAngle = currentStartAngle,
          sweepAngle = sweep,
          useCenter = false,
          topLeft = topLeft,
          size = Size(arcSize, arcSize),
          style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
        currentStartAngle += sweep
      }

      // Dropbox Arc
      if (dropboxSweep > 0f) {
        val sweep = dropboxSweep * animationProgress.value
        drawArc(
          color = CloudProvider.DROPBOX.brandColor,
          startAngle = currentStartAngle,
          sweepAngle = sweep,
          useCenter = false,
          topLeft = topLeft,
          size = Size(arcSize, arcSize),
          style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
      }
    }

    // Center Content
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = "${summary.usedPercentage.toInt()}%",
        style = MaterialTheme.typography.headlineMedium.copy(
          fontWeight = FontWeight.Bold,
          letterSpacing = (-0.5).sp
        ),
        color = Color(0xFF1A1C1E)
      )
      Text(
        text = "USED",
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.5.sp
        ),
        color = Color(0xFF44474E)
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = CloudStorageRepository.formatBytes(summary.totalUsedBytes),
        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
        color = Color(0xFF0061A4)
      )
    }
  }
}
