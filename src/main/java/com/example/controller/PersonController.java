package com.example.controller;


import com.example.model.Person;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@EnableSqs
@RestController("/persons")
public class PersonController {

    static final String QUEUE_NAME = "https://sqs.us-east-2.amazonaws.com/484959649436/DevelopesQueue";

    @Autowired
    private QueueMessagingTemplate messagingTemplate;


    @PostMapping("/person")
    public ResponseEntity save(@RequestBody Person requestDto){

        messagingTemplate.convertAndSend(
                QUEUE_NAME,new Person("John", "Doe"));
       return ResponseEntity.ok().build();
    }


    @SqsListener(value = QUEUE_NAME, deletionPolicy = SqsMessageDeletionPolicy.NEVER)
    public void process( final  Person person, @Header("SenderId") String senderId) throws Exception {
        log.info("---  Received message: {}, having SenderId: {}", person, senderId);

        int a ;

//        @SqsListener(value = "${sqs.queueNames.point}")
//        public void receive(String message, @Header("SenderId") String senderId) throws IOException {
//            log.info("senderId: {}, message: {}", senderId, message);
//            PointDto messageObject = objectMapper.readValue(message, PointDto.class);
//            pointRepository.save(messageObject.toEntity());
//        }

    }


}
