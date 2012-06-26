package org.eclipse.jetty.websocket.server.handshake;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.websocket.api.AcceptHash;
import org.eclipse.jetty.websocket.extensions.Extension;
import org.eclipse.jetty.websocket.server.WebSocketServer;

/**
 * WebSocket Handshake for <a href="https://tools.ietf.org/html/rfc6455">RFC 6455</a>.
 */
public class HandshakeRFC6455 implements WebSocketServer.Handshake
{
    /** RFC 6455 - Sec-WebSocket-Version */
    public static final int VERSION = 13;

    @Override
    public void doHandshakeResponse(HttpServletRequest request, HttpServletResponse response, List<Extension> extensions, String acceptedSubProtocol)
    {
        String key = request.getHeader("Sec-WebSocket-Key");

        if (key == null)
        {
            throw new IllegalStateException("Missing request header 'Sec-WebSocket-Key'");
        }

        // build response
        response.setHeader("Upgrade","WebSocket");
        response.addHeader("Connection","Upgrade");
        response.addHeader("Sec-WebSocket-Accept",AcceptHash.hashKey(key));

        if (acceptedSubProtocol != null)
        {
            response.addHeader("Sec-WebSocket-Protocol",acceptedSubProtocol);
        }

        if (extensions != null)
        {
            for (Extension ext : extensions)
            {
                response.addHeader("Sec-WebSocket-Extensions",ext.getConfig().getParameterizedName());
            }
        }

        response.setStatus(HttpServletResponse.SC_SWITCHING_PROTOCOLS);
    }
}