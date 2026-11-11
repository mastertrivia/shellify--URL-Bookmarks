package io.shellify.app.presentation.share

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import io.shellify.app.core.deeplink.DeepLinkHandler
import io.shellify.app.core.deeplink.QrCodeGenerator
import io.shellify.app.domain.model.WebApp
import io.shellify.app.presentation.components.AppIcon
import io.shellify.app.presentation.theme.Dimens
import io.shellify.core.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppShareSheet(
    appName: String,
    appUrl: String,
    onDismiss: () -> Unit,
    shareWebsiteUrl: Boolean = true,
    app: WebApp? = null,
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val customLink =
        remember(appUrl, appName) { DeepLinkHandler.buildCustomScheme(appUrl, appName) }
    val httpsLink = remember(appUrl, appName) { DeepLinkHandler.buildHttps(appUrl, appName) }
    val openLink = remember(appUrl) { DeepLinkHandler.buildOpen(appUrl) }
    val content = remember(appUrl, customLink, httpsLink, openLink, shareWebsiteUrl) {
        shareSheetContent(
            appUrl = appUrl,
            customLink = customLink,
            httpsLink = httpsLink,
            openLink = openLink,
            shareWebsiteUrl = shareWebsiteUrl,
        )
    }
    val qrBitmap = remember(customLink, content.showQrCode) {
        if (content.showQrCode) QrCodeGenerator.generate(customLink) else null
    }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spaceLg)
                .padding(bottom = Dimens.spaceLg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceMd),
        ) {
            if (app != null) {
                AppIcon(app = app, modifier = Modifier.size(Dimens.sizeApp))
            }
            Text(
                appName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (qrBitmap != null) {
                Image(
                    bitmap = qrBitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.size(Dimens.sizeQrCode),
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceXs),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    content.displayedUrl,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
                if (content.showHttpsLink && content.httpsLink != null) {
                    Text(
                        content.httpsLink,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSm)) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        cb.setPrimaryClip(ClipData.newPlainText("link", content.copyLinkText))
                    },
                ) { Text(stringResource(R.string.share_copy_link)) }
                if (content.copyAppLinkText != null) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            cb.setPrimaryClip(ClipData.newPlainText("Shellify app link", content.copyAppLinkText))
                        },
                    ) { Text(stringResource(R.string.share_copy_app_link)) }
                }
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, content.shareText)
                        }
                        context.startActivity(Intent.createChooser(intent, appName))
                    },
                ) { Text(stringResource(R.string.share_button)) }
            }
        }
    }
}
