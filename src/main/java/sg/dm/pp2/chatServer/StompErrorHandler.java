package sg.dm.pp2.chatServer;

import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class StompErrorHandler extends StompSubProtocolErrorHandler {
//
//    /**
//     * 클라이언트 메시지 처리 중에 발생한 오류를 처리
//     *
//     * @param clientMessage 클라이언트 메시지
//     * @param ex 발생한 예외
//     * @return 오류 메시지를 포함한 Message 객체
//     */
//    @Override
//    public Message<byte[]> handleClientMessageProcessingError(
//            Message<byte[]> clientMessage,
//            Throwable ex) {
//
//        // 오류 메시지가 "UNAUTHORIZED"인 경우 - throw new MessageDeliveryException("UNAUTHORIZED")
//        if ("UNAUTHORIZED".equals(ex.getMessage())) {
//            return errorMessage("유효하지 않은 권한입니다.");
//        }
//
//        return super.handleClientMessageProcessingError(clientMessage, ex);
//    }
//
//    /**
//     * 오류 메시지를 포함한 Message 객체를 생성
//     *
//     * @param errorMessage 오류 메시지
//     * @return 오류 메시지를 포함한 Message 객체
//     */
//    private Message<byte[]> errorMessage(String errorMessage) {
//
//        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
//        accessor.setLeaveMutable(true);
//
//        return MessageBuilder.createMessage(errorMessage.getBytes(StandardCharsets.UTF_8),
//                accessor.getMessageHeaders());
//    }
}