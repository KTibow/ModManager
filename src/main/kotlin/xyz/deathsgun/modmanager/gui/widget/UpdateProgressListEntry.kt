package xyz.deathsgun.modmanager.gui.widget

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.hud.BackgroundHelper.ColorMixer
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ScreenTexts
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import xyz.deathsgun.modmanager.ModManager
import xyz.deathsgun.modmanager.api.gui.list.ListWidget
import xyz.deathsgun.modmanager.update.ProgressListener
import xyz.deathsgun.modmanager.update.Update

@OptIn(DelicateCoroutinesApi::class)
class UpdateProgressListEntry(list: ListWidget<UpdateProgressListEntry>, val update: Update) :
    ListWidget.Entry<UpdateProgressListEntry>(list, update.mod.id), ProgressListener {

    internal var progress = 0.0
    private var pos = 0

    init {
        GlobalScope.launch {
            delay(200)
            ModManager.modManager.update.updateMod(update) { this@UpdateProgressListEntry.progress = it }
        }
    }

    override fun render(
        matrices: MatrixStack,
        index: Int,
        y: Int,
        x: Int,
        entryWidth: Int,
        entryHeight: Int,
        mouseX: Int,
        mouseY: Int,
        hovered: Boolean,
        tickDelta: Float
    ) {
        val textRenderer = MinecraftClient.getInstance().textRenderer
        textRenderer.draw(matrices, update.mod.name, x.toFloat(), y + 1f, 0xFFFFFF)
        val nameWidth = textRenderer.getWidth(update.mod.name) + 5
        if (progress == 1.0) {
            textRenderer.draw(matrices, ScreenTexts.DONE, (x + nameWidth).toFloat(), y + 1f, 0xFFFFFF)
            return
        }
        renderProgressBar(matrices, entryWidth - nameWidth, x + nameWidth, y, x + entryWidth, y + entryHeight)
    }

    fun tick() {
        pos += 5
    }

    override fun onProgress(progress: Double) {
        this.progress = progress
    }

    private fun renderProgressBar(matrices: MatrixStack, width: Int, minX: Int, minY: Int, maxX: Int, maxY: Int) {
        val color = ColorMixer.getArgb(255, 255, 255, 255)
        var barWidth = width / 10
        val overlap = (minX + pos + barWidth) - maxX + 2
        if (overlap > 0) {
            barWidth -= overlap
        }
        if ((minX + pos) - maxX + 2 > 0) {
            pos = 0
        }
        Screen.fill(matrices, minX + 2 + pos, minY + 2, minX + pos + barWidth, maxY - 2, color)
        Screen.fill(matrices, minX + 1, minY, maxX - 1, minY + 1, color)
        Screen.fill(matrices, minX + 1, maxY, maxX - 1, maxY - 1, color)
        Screen.fill(matrices, minX, minY, minX + 1, maxY, color)
        Screen.fill(matrices, maxX, minY, maxX - 1, maxY, color)
    }

    override fun getNarration(): Text {
        return LiteralText.EMPTY
    }
}