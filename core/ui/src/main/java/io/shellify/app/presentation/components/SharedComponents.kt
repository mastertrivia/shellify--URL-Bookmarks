package io.shellify.app.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import io.shellify.core.ui.R
import io.shellify.app.core.iconpack.SimpleIconEntry
import io.shellify.app.domain.model.WebApp
import io.shellify.app.presentation.theme.Dimens

/**
 * Unified bordered card used in both the Add and Settings screens.
 * Provides only the card shell (shape, border, elevation, colors) — callers control inner layout.
 */
@Composable
fun SurfaceCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.cornerXl),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(Dimens.borderDefault, MaterialTheme.colorScheme.outlineVariant),
        content = content,
    )
}

/**
 * Generic confirm/dismiss dialog.
 *
 * @param title    Dialog headline text.
 * @param body     Dialog body text.
 * @param confirmLabel  Label for the confirm button.
 * @param onConfirm Callback invoked when the user taps the confirm button.
 * @param onDismiss Callback invoked on dismiss or cancel.
 * @param icon     Optional leading icon shown above the title.
 * @param isDestructive When true, the confirm button is styled with [MaterialTheme.colorScheme.error].
 */
@Composable
fun ConfirmDialog(
    title: String,
    body: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    icon: ImageVector? = null,
    isDestructive: Boolean = false,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = if (icon != null) ({ Icon(icon, null) }) else null,
        title = { Text(title) },
        text = { Text(body) },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = if (isDestructive) {
                    ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                } else {
                    ButtonDefaults.textButtonColors()
                },
            ) { Text(confirmLabel) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.common_cancel)) }
        },
    )
}

/**
 * Destructive confirm dialog that stays disabled until [confirmationCode] is typed exactly.
 *
 * @param title              Dialog headline text.
 * @param body               Dialog body text.
 * @param confirmLabel       Label for the confirm button.
 * @param confirmationCode   Exact code the user must type to enable confirm.
 * @param confirmationLabel  Visible label for the code field.
 * @param confirmationHint   Helper text shown under the code field.
 * @param onConfirm          Callback invoked when the user taps the enabled confirm button.
 * @param onDismiss          Callback invoked on dismiss or cancel.
 * @param icon               Optional leading icon shown above the title.
 */
@Composable
fun TypedConfirmDialog(
    title: String,
    body: String,
    confirmLabel: String,
    confirmationCode: String,
    confirmationLabel: String,
    confirmationHint: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    icon: ImageVector? = null,
) {
    var typed by remember { mutableStateOf("") }
    val isConfirmed = typed == confirmationCode
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = if (icon != null) ({ Icon(icon, null) }) else null,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.spaceMd)) {
                Text(body)
                OutlinedTextField(
                    value = typed,
                    onValueChange = { typed = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text(confirmationLabel) },
                    placeholder = {
                        Text(
                            confirmationCode,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
                        )
                    },
                    supportingText = {
                        Text(
                            confirmationHint,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Characters,
                        keyboardType = KeyboardType.Ascii,
                    ),
                    shape = RoundedCornerShape(Dimens.cornerMd),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = isConfirmed,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
            ) { Text(confirmLabel) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.common_cancel)) }
        },
    )
}

private data class LanguageOption(val code: String, val nativeName: String, val englishName: String)

private val languageOptions = listOf(
    LanguageOption("en", "English", "English"),
    LanguageOption("fr", "Français", "French"),
    LanguageOption("ar", "العربية", "Arabic"),
)

fun languageDisplayName(code: String): String =
    languageOptions.find { it.code == code }?.nativeName ?: code

/**
 * Bottom sheet for choosing the app display language.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguagePickerDialog(
    currentLanguage: String,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Text(
            stringResource(R.string.language_dialog_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(
                start = Dimens.spaceLg,
                end = Dimens.spaceLg,
                bottom = Dimens.spaceSm
            ),
        )
        Column(
            modifier = Modifier.padding(horizontal = Dimens.spaceMd),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceXxs),
        ) {
            languageOptions.forEach { lang ->
                val isSelected = currentLanguage == lang.code
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(Dimens.cornerLg))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                        )
                        .clickable { onSelect(lang.code) }
                        .padding(horizontal = Dimens.spaceLg, vertical = Dimens.spaceMd),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            lang.nativeName,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            lang.englishName,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                alpha = 0.7f
                            )
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    RadioButton(
                        selected = isSelected,
                        onClick = { onSelect(lang.code) },
                    )
                }
            }
        }
        Spacer(Modifier.height(Dimens.spaceXl))
    }
}

/**
 * Concentric-rings + dashed orbit + centre tile + ghost boxes illustration
 * reused across Home, Shortcuts, and Category empty-state screens.
 *
 * @param centerIcon Icon rendered in the centre tile of the illustration.
 * @param modifier   Optional modifier applied to the outer 160×160 [Box].
 */
@Composable
fun EmptyStateIllustration(
    centerIcon: ImageVector,
    modifier: Modifier = Modifier,
) {
    val p97 = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
    val p95 = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
    val p90 = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.70f)
    val p40 = MaterialTheme.colorScheme.primary
    val surfDim = MaterialTheme.colorScheme.outlineVariant
    val surface = MaterialTheme.colorScheme.surface

    Box(
        modifier = modifier.size(Dimens.illustrationSize),
        contentAlignment = Alignment.Center,
    ) {
        // Outermost tinted ring
        Box(
            modifier = Modifier
                .size(Dimens.illustrationSize)
                .background(p97, CircleShape)
        )
        // Middle ring
        Box(
            modifier = Modifier
                .size(Dimens.illustrationSizeMid)
                .background(p95, CircleShape)
        )
        // Inner ring
        Box(
            modifier = Modifier
                .size(Dimens.illustrationSizeInner)
                .background(p90, CircleShape)
        )
        // Single dashed orbit at r=70
        Canvas(modifier = Modifier.size(Dimens.illustrationSize)) {
            drawCircle(
                color = p40.copy(alpha = 0.35f),
                radius = Dimens.illustrationRadius.toPx(),
                style = Stroke(
                    width = 1.2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(
                        floatArrayOf(3.dp.toPx(), 7.dp.toPx()), 0f,
                    ),
                ),
            )
        }
        // Center tile: 64dp / radius 20dp / icon 30dp
        Box(
            modifier = Modifier
                .size(Dimens.sizeIllustrationTile)
                .background(p40, RoundedCornerShape(Dimens.corner20)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                centerIcon,
                null,
                modifier = Modifier.size(Dimens.sizeIconLarge),
                tint = Color.White,
            )
        }
        // Ghost tiles — positions match design handoff absolute coords
        Box(
            modifier = Modifier
                .size(Dimens.size2xl)
                .offset(x = (-49).dp, y = (-57).dp)
                .background(surface, RoundedCornerShape(Dimens.cornerSm))
                .border(Dimens.borderDefault, surfDim, RoundedCornerShape(Dimens.cornerSm)),
        )
        Box(
            modifier = Modifier
                .size(Dimens.sizeLg)
                .offset(x = 57.dp, y = (-45).dp)
                .background(surface, RoundedCornerShape(Dimens.cornerSm))
                .border(Dimens.borderDefault, surfDim, RoundedCornerShape(Dimens.cornerSm)),
        )
        Box(
            modifier = Modifier
                .size(Dimens.sizeLg)
                .offset(x = (-61).dp, y = 51.dp)
                .background(surface, RoundedCornerShape(Dimens.cornerSm))
                .border(Dimens.borderDefault, surfDim, RoundedCornerShape(Dimens.cornerSm)),
        )
        Box(
            modifier = Modifier
                .size(Dimens.size2xl)
                .offset(x = 41.dp, y = 59.dp)
                .background(surface, RoundedCornerShape(Dimens.cornerSm))
                .border(Dimens.borderDefault, surfDim, RoundedCornerShape(Dimens.cornerSm)),
        )
    }
}

@Composable
fun AppIcon(app: WebApp, modifier: Modifier = Modifier) {
    if (app.iconPath != null) {
        AsyncImage(
            model = app.iconPath,
            contentDescription = app.name,
            contentScale = ContentScale.Crop,
            modifier = modifier.clip(RoundedCornerShape(Dimens.cornerLg)),
        )
    } else {
        val color = runCatching { Color(android.graphics.Color.parseColor(app.themeColor)) }
            .getOrDefault(MaterialTheme.colorScheme.primary)
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(Dimens.cornerLg))
                .background(color),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = app.name.firstOrNull { it.isLetterOrDigit() }?.uppercaseChar()?.toString()
                    ?: "P",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleIconPickerSheet(
    icons: List<SimpleIconEntry>,
    query: String,
    isLoading: Boolean,
    onQueryChange: (String) -> Unit,
    onSelect: (SimpleIconEntry, Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val primaryArgb = MaterialTheme.colorScheme.primary.toArgb()
    val svgLoader = remember {
        ImageLoader.Builder(context).components { add(SvgDecoder.Factory()) }.build()
    }
    val filtered = remember(icons, query) {
        if (query.isBlank()) icons else icons.filter { it.title.contains(query, ignoreCase = true) }
    }
    val iconBgColor = MaterialTheme.colorScheme.primary

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Text(
            stringResource(R.string.add_icon_pack_picker_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(
                start = Dimens.spaceLg,
                end = Dimens.spaceLg,
                bottom = Dimens.spaceSm
            ),
        )
        Column(
            modifier = Modifier.padding(horizontal = Dimens.spaceLg),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceSm),
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text(stringResource(R.string.add_icon_pack_search_hint)) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        null,
                        modifier = Modifier.size(Dimens.sizeMd)
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(Dimens.cornerMd),
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Dimens.cornerXl),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = BorderStroke(
                    Dimens.borderDefault,
                    MaterialTheme.colorScheme.outlineVariant
                ),
            ) {
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Dimens.heroHeightSm),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(Dimens.sizeEmptyIcon),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimens.spaceSm),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceXs),
                        verticalArrangement = Arrangement.spacedBy(Dimens.spaceXs),
                    ) {
                        items(filtered, key = { it.slug }) { entry ->
                            Column(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(Dimens.cornerMd))
                                    .clickable { onSelect(entry, primaryArgb) }
                                    .padding(Dimens.spaceXxs),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(Dimens.spaceXxs),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(Dimens.sizeIconHero)
                                        .clip(RoundedCornerShape(Dimens.cornerMd))
                                        .background(iconBgColor),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    AsyncImage(
                                        model = "https://cdn.jsdelivr.net/npm/simple-icons/icons/${entry.slug}.svg",
                                        contentDescription = entry.title,
                                        imageLoader = svgLoader,
                                        modifier = Modifier.size(Dimens.size5xl),
                                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                                            Color.White,
                                            androidx.compose.ui.graphics.BlendMode.SrcIn,
                                        ),
                                    )
                                }
                                Text(
                                    entry.title,
                                    style = MaterialTheme.typography.labelSmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(Dimens.spaceXl))
    }
}
