package de.caritas.cob.liveservice.websocket.service;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import de.caritas.cob.liveservice.websocket.model.WebSocketUserSession;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class WebSocketSessionIdResolverTest {

  @InjectMocks
  private WebSocketSessionIdResolver sessionIdResolver;

  @Mock
  private SocketUserRegistry socketUserRegistry;

  @Test
  public void resolveUserIds_Should_returnEmptyList_When_userIdsAreNull() {
    List<String> sessionIds = this.sessionIdResolver.resolveUserIds(null);

    assertThat(sessionIds, hasSize(0));
  }

  @Test
  public void resolveUserIds_Should_returnEmptyList_When_userIdsAreEmpty() {
    List<String> sessionIds = this.sessionIdResolver.resolveUserIds(emptyList());

    assertThat(sessionIds, hasSize(0));
  }

  @Test
  public void resolveUserIds_Should_returnEmptyList_When_noUserIsRegistered() {
    List<String> userIds = asList("1", "2", "3");
    List<WebSocketUserSession> registeredSessions = asList(
        userSession("4", "41"),
        userSession("5", "51"),
        userSession("6", "61")
    );
    when(socketUserRegistry.retrieveAllUsers()).thenReturn(registeredSessions);

    List<String> sessionIds = this.sessionIdResolver.resolveUserIds(userIds);

    assertThat(sessionIds, hasSize(0));
  }

  private WebSocketUserSession userSession(String userId, String sessionId) {
    return WebSocketUserSession.builder()
        .userId(userId)
        .websocketSessionId(sessionId)
        .build();
  }

  @Test
  public void resolveUserIds_Should_returnResolvedSessions_When_usersAreRegistered() {
    List<String> userIds = asList("1", "2", "3", "4", "5", "6", "7", "8", "9");
    List<WebSocketUserSession> registeredSessions = asList(
        userSession("2", "21"),
        userSession("4", "41"),
        userSession("7", "71"),
        userSession("9", "91")
    );
    when(socketUserRegistry.retrieveAllUsers()).thenReturn(registeredSessions);

    List<String> sessionIds = this.sessionIdResolver.resolveUserIds(userIds);

    assertThat(sessionIds, hasSize(4));
    assertThat(sessionIds.get(0), is("21"));
    assertThat(sessionIds.get(1), is("41"));
    assertThat(sessionIds.get(2), is("71"));
    assertThat(sessionIds.get(3), is("91"));
  }

  @Test
  public void resolveUserIds_Should_returnResolvedSessions_When_registryContainsNullUserIds() {
    List<String> userIds = asList("1", "2", "3", "4", "5", "6", "7", "8", "9");
    List<WebSocketUserSession> registeredSessions = asList(
        userSession("2", "21"),
        userSession(null, "41"),
        userSession("7", "71"),
        userSession(null, "91")
    );
    when(socketUserRegistry.retrieveAllUsers()).thenReturn(registeredSessions);

    List<String> sessionIds = this.sessionIdResolver.resolveUserIds(userIds);

    assertThat(sessionIds, hasSize(2));
    assertThat(sessionIds.get(0), is("21"));
    assertThat(sessionIds.get(1), is("71"));
  }

}
