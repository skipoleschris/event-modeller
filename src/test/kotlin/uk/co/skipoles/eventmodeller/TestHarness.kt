package uk.co.skipoles.eventmodeller

import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Graphics
import java.awt.image.BufferedImage
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JScrollPane
import uk.co.skipoles.eventmodeller.definition.DefinitionParser
import uk.co.skipoles.eventmodeller.visualisation.VisualisationModelGenerator
import uk.co.skipoles.eventmodeller.visualisation.image.SvgDocumentGenerator
import uk.co.skipoles.eventmodeller.visualisation.image.asPNG

fun main() {
  val definition = DefinitionParser.parse(definitionText)
  val model = VisualisationModelGenerator.generate(definition.getOrThrow())
  val svgGenerator = SvgDocumentGenerator(model)

  val panel = ImagePanel(svgGenerator.renderDocument().asPNG())
  val scroller = JScrollPane()
  scroller.setViewportView(panel)
  scroller.preferredSize = Dimension(2048, 1200)
  val frame = JFrame("Test")
  frame.contentPane.layout = FlowLayout()
  frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

  frame.add(scroller)
  frame.pack()
  frame.isVisible = true
}

class ImagePanel(private val image: BufferedImage) : JPanel(true) {
  init {
    preferredSize = Dimension(image.width, image.height)
  }

  override fun paintComponent(g: Graphics?) {
    super.paintComponent(g)
    g?.drawImage(image, 0, 0, null)
  }
}

val definitionText =
    """
    [c] uk.co.skipoles.clashcat.sagas.RegisterClanCommand -> [e] uk.co.skipoles.clashcat.sagas.ClanRegistrationReceivedEvent
    [e] uk.co.skipoles.clashcat.sagas.ClanRegistrationReceivedEvent -> [s] uk.co.skipoles.clashcat.sagas.ClanRegistrationSaga
    [s] uk.co.skipoles.clashcat.sagas.ClanRegistrationSaga -> [c] uk.co.skipoles.clashcat.clashapi.DoesClanExistCommand
    [c] uk.co.skipoles.clashcat.clashapi.DoesClanExistCommand -> [e] uk.co.skipoles.clashcat.clashapi.ClanLookupCompletedEvent
    [e] uk.co.skipoles.clashcat.clashapi.ClanLookupCompletedEvent -> [s] uk.co.skipoles.clashcat.sagas.ClanRegistrationSaga
    [s] uk.co.skipoles.clashcat.sagas.ClanRegistrationSaga -> [c] uk.co.skipoles.clashcat.clan.TrackClanCommand
    [c] uk.co.skipoles.clashcat.clan.TrackClanCommand -> [a] uk.co.skipoles.clashcat.clan.Clan :: [e] uk.co.skipoles.clashcat.clan.ClanRegisteredEvent
    [a] uk.co.skipoles.clashcat.clan.Clan :: [e] uk.co.skipoles.clashcat.clan.ClanRegisteredEvent -> [s] uk.co.skipoles.clashcat.sagas.ClanRegistrationSaga
    [s] uk.co.skipoles.clashcat.sagas.ClanRegistrationSaga -> [c] uk.co.skipoles.clashcat.clashapi.ObtainClanDataFromCoCApiCommand
    [c] uk.co.skipoles.clashcat.clashapi.ObtainClanDataFromCoCApiCommand -> [e] uk.co.skipoles.clashcat.clashapi.ClanDataObtainedEvent
    [e] uk.co.skipoles.clashcat.clashapi.ClanDataObtainedEvent -> [s] uk.co.skipoles.clashcat.sagas.ClanUpdateSaga
    [s] uk.co.skipoles.clashcat.sagas.ClanUpdateSaga -> [c] uk.co.skipoles.clashcat.clan.UpdateFromClanDataCommand
    [c] uk.co.skipoles.clashcat.clan.UpdateFromClanDataCommand -> [a] uk.co.skipoles.clashcat.clan.Clan :: [e] uk.co.skipoles.clashcat.clan.NewMemberJoinedEvent
    [a] uk.co.skipoles.clashcat.clan.Clan :: [e] uk.co.skipoles.clashcat.clan.NewMemberJoinedEvent -> [v] uk.co.skipoles.clashcat.clan.ClanMemberProjection
    [a] uk.co.skipoles.clashcat.clan.Clan :: [e] uk.co.skipoles.clashcat.clan.NewMemberJoinedEvent -> [s] uk.co.skipoles.clashcat.sagas.NewMemberJoinedSaga
    [s] uk.co.skipoles.clashcat.sagas.NewMemberJoinedSaga -> [c] uk.co.skipoles.clashcat.player.TrackPlayerCommand
    [c] uk.co.skipoles.clashcat.player.TrackPlayerCommand -> [a] uk.co.skipoles.clashcat.player.Player :: [e] uk.co.skipoles.clashcat.player.PlayerTrackedEvent
    [a] uk.co.skipoles.clashcat.player.Player :: [e] uk.co.skipoles.clashcat.player.PlayerTrackedEvent -> [v] uk.co.skipoles.clashcat.player.PlayerSummaryProjection
    [a] uk.co.skipoles.clashcat.player.Player :: [e] uk.co.skipoles.clashcat.player.PlayerTrackedEvent -> [s] uk.co.skipoles.clashcat.sagas.NewMemberJoinedSaga
    [s] uk.co.skipoles.clashcat.sagas.NewMemberJoinedSaga -> [c] uk.co.skipoles.clashcat.clashapi.ObtainPlayerDataFromCoCApiCommand
    [c] uk.co.skipoles.clashcat.clashapi.ObtainPlayerDataFromCoCApiCommand -> [e] uk.co.skipoles.clashcat.clashapi.PlayerDataObtainedEvent
    [e] uk.co.skipoles.clashcat.clashapi.PlayerDataObtainedEvent -> [s] uk.co.skipoles.clashcat.sagas.PlayerUpdateSaga
    [s] uk.co.skipoles.clashcat.sagas.PlayerUpdateSaga -> [c] uk.co.skipoles.clashcat.player.UpdateFromPlayerDataCommand
    [c] uk.co.skipoles.clashcat.player.UpdateFromPlayerDataCommand -> [a] uk.co.skipoles.clashcat.player.Player :: [e] uk.co.skipoles.clashcat.player.PlayerLastActiveEvent
    [a] uk.co.skipoles.clashcat.player.Player :: [e] uk.co.skipoles.clashcat.player.PlayerLastActiveEvent -> [v] uk.co.skipoles.clashcat.player.PlayerSummaryProjection
    [a] uk.co.skipoles.clashcat.player.Player :: [e] uk.co.skipoles.clashcat.player.PlayerLastActiveEvent -> [v] uk.co.skipoles.clashcat.clan.ClanMemberProjection
    [c] uk.co.skipoles.clashcat.player.UpdateFromPlayerDataCommand -> [a] uk.co.skipoles.clashcat.player.Player :: [e] uk.co.skipoles.clashcat.player.PlayerChangedWarPreferenceEvent
    [a] uk.co.skipoles.clashcat.player.Player :: [e] uk.co.skipoles.clashcat.player.PlayerChangedWarPreferenceEvent -> [v] uk.co.skipoles.clashcat.player.PlayerSummaryProjection
    [a] uk.co.skipoles.clashcat.player.Player :: [e] uk.co.skipoles.clashcat.player.PlayerChangedWarPreferenceEvent -> [v] uk.co.skipoles.clashcat.clan.ClanMemberProjection
    [c] uk.co.skipoles.clashcat.player.UpdateFromPlayerDataCommand -> [a] uk.co.skipoles.clashcat.player.Player :: [e] uk.co.skipoles.clashcat.player.PlayerChangedNameEvent
    [a] uk.co.skipoles.clashcat.player.Player :: [e] uk.co.skipoles.clashcat.player.PlayerChangedNameEvent -> [v] uk.co.skipoles.clashcat.player.PlayerSummaryProjection
    [c] uk.co.skipoles.clashcat.player.UpdateFromPlayerDataCommand -> [a] uk.co.skipoles.clashcat.player.Player :: [e] uk.co.skipoles.clashcat.player.PlayerMetricsChangedEvent
    [a] uk.co.skipoles.clashcat.player.Player :: [e] uk.co.skipoles.clashcat.player.PlayerMetricsChangedEvent -> [v] uk.co.skipoles.clashcat.player.PlayerSummaryProjection
    [c] uk.co.skipoles.clashcat.player.UpdateFromPlayerDataCommand -> [a] uk.co.skipoles.clashcat.player.Player :: [e] uk.co.skipoles.clashcat.player.PlayerActivityChangedEvent
    [c] uk.co.skipoles.clashcat.player.UpdateFromPlayerDataCommand -> [a] uk.co.skipoles.clashcat.player.Player :: [e] uk.co.skipoles.clashcat.player.PlayerDonationsChangedEvent
    [c] uk.co.skipoles.clashcat.player.UpdateFromPlayerDataCommand -> [a] uk.co.skipoles.clashcat.player.Player :: [e] uk.co.skipoles.clashcat.player.PlayerDonationsSeasonCompleteEvent
    [c] uk.co.skipoles.clashcat.player.UpdateFromPlayerDataCommand -> [a] uk.co.skipoles.clashcat.player.Player :: [e] uk.co.skipoles.clashcat.player.PlayerUpdatedEvent
    [a] uk.co.skipoles.clashcat.player.Player :: [e] uk.co.skipoles.clashcat.player.PlayerUpdatedEvent -> [s] uk.co.skipoles.clashcat.sagas.PlayerUpdateSaga
    [a] uk.co.skipoles.clashcat.player.Player :: [e] uk.co.skipoles.clashcat.player.PlayerUpdatedEvent -> [s] uk.co.skipoles.clashcat.sagas.NewMemberJoinedSaga
    [e] uk.co.skipoles.clashcat.clashapi.PlayerDataObtainedEvent -> [s] uk.co.skipoles.clashcat.sagas.NewMemberJoinedSaga
    [s] uk.co.skipoles.clashcat.sagas.NewMemberJoinedSaga -> [c] uk.co.skipoles.clashcat.player.UpdateFromPlayerDataCommand
    [c] uk.co.skipoles.clashcat.clan.UpdateFromClanDataCommand -> [a] uk.co.skipoles.clashcat.clan.Clan :: [e] uk.co.skipoles.clashcat.clan.ClanInformationChangedEvent
    [a] uk.co.skipoles.clashcat.clan.Clan :: [e] uk.co.skipoles.clashcat.clan.ClanInformationChangedEvent -> [v] uk.co.skipoles.clashcat.clan.ClanSummaryProjection
    [c] uk.co.skipoles.clashcat.clan.UpdateFromClanDataCommand -> [a] uk.co.skipoles.clashcat.clan.Clan :: [e] uk.co.skipoles.clashcat.clan.MemberDetailsChangedEvent
    [a] uk.co.skipoles.clashcat.clan.Clan :: [e] uk.co.skipoles.clashcat.clan.MemberDetailsChangedEvent -> [v] uk.co.skipoles.clashcat.clan.ClanMemberProjection
    [c] uk.co.skipoles.clashcat.clan.UpdateFromClanDataCommand -> [a] uk.co.skipoles.clashcat.clan.Clan :: [e] uk.co.skipoles.clashcat.clan.MemberLeftEvent
    [a] uk.co.skipoles.clashcat.clan.Clan :: [e] uk.co.skipoles.clashcat.clan.MemberLeftEvent -> [v] uk.co.skipoles.clashcat.clan.ClanMemberProjection
    [a] uk.co.skipoles.clashcat.clan.Clan :: [e] uk.co.skipoles.clashcat.clan.MemberLeftEvent -> [c] uk.co.skipoles.clashcat.player.StopTrackingPlayerCommand
    [c] uk.co.skipoles.clashcat.player.StopTrackingPlayerCommand -> [a] uk.co.skipoles.clashcat.player.Player :: [e] uk.co.skipoles.clashcat.player.StoppedTrackingPlayerEvent
    [a] uk.co.skipoles.clashcat.player.Player :: [e] uk.co.skipoles.clashcat.player.StoppedTrackingPlayerEvent -> [v] uk.co.skipoles.clashcat.player.PlayerSummaryProjection
    [c] uk.co.skipoles.clashcat.clan.UpdateFromClanDataCommand -> [a] uk.co.skipoles.clashcat.clan.Clan :: [e] uk.co.skipoles.clashcat.clan.ClanUpdatedEvent
    [a] uk.co.skipoles.clashcat.clan.Clan :: [e] uk.co.skipoles.clashcat.clan.ClanUpdatedEvent -> [s] uk.co.skipoles.clashcat.sagas.ClanRegistrationSaga
    [a] uk.co.skipoles.clashcat.clan.Clan :: [e] uk.co.skipoles.clashcat.clan.ClanUpdatedEvent -> [s] uk.co.skipoles.clashcat.sagas.ClanUpdateSaga
    [e] uk.co.skipoles.clashcat.clashapi.ClanDataObtainedEvent -> [s] uk.co.skipoles.clashcat.sagas.ClanRegistrationSaga
    [s] uk.co.skipoles.clashcat.sagas.ClanRegistrationSaga -> [c] uk.co.skipoles.clashcat.clan.UpdateFromClanDataCommand
    """.trimIndent()
