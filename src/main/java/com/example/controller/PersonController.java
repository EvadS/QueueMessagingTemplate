package com.example.controller;


import com.example.entity.Person;
import com.example.model.PersonRequest;
import com.example.repository.PersonRepository;
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
@AllArgsConstructor
public class PersonController {

    static final String QUEUE_NAME = "https://sqs.us-east-2.amazonaws.com/484959649436/DevelopesQueue";

    private final QueueMessagingTemplate messagingTemplate;
    private final PersonRepository repository;

    @PostMapping("/person")
    public ResponseEntity save(@RequestBody PersonRequest personRequest){

        messagingTemplate.convertAndSend(QUEUE_NAME,personRequest);
       return ResponseEntity.ok().build();
    }


    /**
     * it just for test
     * this method might be on different project
     * @param personRequest
     * @param senderId
     * @throws Exception
     */
    @SqsListener(value = QUEUE_NAME, deletionPolicy = SqsMessageDeletionPolicy.NEVER)
    public void process(final PersonRequest personRequest, @Header("SenderId") String senderId) throws Exception {
        log.info("---  Received message: {}, having SenderId: {}", personRequest, senderId);

        Person person = new Person();
        person.setLname(personRequest.getLname());
        person.setName(personRequest.getName());

        repository.save(person);

        log.info("->  Stored to data base : {}" , person);



//        @SqsListener(value = "${sqs.queueNames.point}")
//        public void receive(String message, @Header("SenderId") String senderId) throws IOException {
//            log.info("senderId: {}, message: {}", senderId, message);
//            PointDto messageObject = objectMapper.readValue(message, PointDto.class);
//            pointRepository.save(messageObject.toEntity());
//        }

    }


}
