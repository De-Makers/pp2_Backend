package sg.dm.pp2.chatServer;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.util.CollectionUtils;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class StompPreHandler implements ChannelInterceptor {
//
//    @Value("${pp2.chat.key}")
//    private String apiKeyName;
//
//    @Value("${pp2.chat.value}")
//    private String apiKeyValue;
//
//    /**
//     * 메시지가 채널로 전송되기 전에 실행
//     *
//     * @param message 메시지 객체
//     * @param channel 메시지 채널
//     * @return 수정된 메시지 객체
//     */
//    @Override
//    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//
//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//
//        // 메시지의 구독 명령이 CONNECT인 경우에만 실행
//        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//            StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
//
//            // 메시지의 헤더에서 'X-API-KEY'으로 지정된 헤더 값을 가져와 'B09ED8799B61ABE6331D3DA5BCFD72ADAFAC5329C3990B3F4075687CAE532CC6'와 비교
//            List<String> headers = headerAccessor.getNativeHeader(apiKeyName);
//            if (CollectionUtils.isEmpty(headers) || !apiKeyValue.equals(headers.get(0))) {
//                throw new MessageDeliveryException("UNAUTHORIZED");
//            }
//        }
//
//        return message;
//    }
}
