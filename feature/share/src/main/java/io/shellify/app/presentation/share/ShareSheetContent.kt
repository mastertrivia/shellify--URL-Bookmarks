package io.shellify.app.presentation.share

data class ShareSheetContent(
    val displayedUrl: String,
    val httpsLink: String?,
    val copyLinkText: String,
    val copyAppLinkText: String?,
    val shareText: String,
    val showQrCode: Boolean,
    val showHttpsLink: Boolean,
)

fun shareSheetContent(
    appUrl: String,
    customLink: String,
    httpsLink: String,
    openLink: String,
    shareWebsiteUrl: Boolean,
): ShareSheetContent {
    if (shareWebsiteUrl) {
        return ShareSheetContent(
            displayedUrl = appUrl,
            httpsLink = null,
            copyLinkText = appUrl,
            copyAppLinkText = null,
            shareText = appUrl,
            showQrCode = false,
            showHttpsLink = false,
        )
    }
    return ShareSheetContent(
        displayedUrl = customLink,
        httpsLink = httpsLink,
        copyLinkText = customLink,
        copyAppLinkText = openLink,
        shareText = "$customLink\n\n$httpsLink",
        showQrCode = true,
        showHttpsLink = true,
    )
}
