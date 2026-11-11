package io.shellify.app.presentation.share

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ShareSheetContentTest {

    private val appUrl = "https://duck.ai"
    private val customLink = "shellify://add?url=abc&name=DuckAI"
    private val httpsLink = "https://shellify.app/add?url=abc&name=DuckAI"
    private val openLink = "shellify://open?url=abc"

    @Test
    fun `website url mode copies and shares the original url without qr or app link`() {
        val content = shareSheetContent(
            appUrl = appUrl,
            customLink = customLink,
            httpsLink = httpsLink,
            openLink = openLink,
            shareWebsiteUrl = true,
        )

        assertEquals(appUrl, content.displayedUrl)
        assertEquals(appUrl, content.copyLinkText)
        assertEquals(appUrl, content.shareText)
        assertNull(content.copyAppLinkText)
        assertFalse(content.showQrCode)
        assertFalse(content.showHttpsLink)
    }

    @Test
    fun `shellify link mode keeps qr code copy-app-link and deep links`() {
        val content = shareSheetContent(
            appUrl = appUrl,
            customLink = customLink,
            httpsLink = httpsLink,
            openLink = openLink,
            shareWebsiteUrl = false,
        )

        assertEquals(customLink, content.displayedUrl)
        assertEquals(customLink, content.copyLinkText)
        assertEquals("$customLink\n\n$httpsLink", content.shareText)
        assertEquals(openLink, content.copyAppLinkText)
        assertTrue(content.showQrCode)
        assertTrue(content.showHttpsLink)
        assertEquals(httpsLink, content.httpsLink)
    }
}
