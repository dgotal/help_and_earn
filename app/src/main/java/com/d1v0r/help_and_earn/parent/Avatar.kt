package com.d1v0r.help_and_earn.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun AvatarItem(
    isSelected: Boolean,
    onAvatarClick: () -> Unit,
    avatarUrl: String,
) {
    Box(
        modifier = Modifier
            .size(240.dp)
            .clip(CircleShape)
            .clickable { onAvatarClick() }
            .padding(32.dp)
            .background(
                if (isSelected) Color.Gray
                else Color.Transparent,
                CircleShape
            )
    ) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = "Avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
fun AvatarSelectionModal(
    avatars: List<String>,
    selectedAvatarUrl: String,
    onAvatarSelected: (String) -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onCancel() },
        title = { Text(text = "Select Avatar") },
        buttons = {
        },
        text = {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                itemsIndexed(avatars) { _, avatarUrl ->
                    AvatarItem(
                        isSelected = avatarUrl == selectedAvatarUrl,
                        onAvatarClick = { onAvatarSelected(avatarUrl) },
                        avatarUrl = avatarUrl,
                    )
                }
            }
        }
    )
}