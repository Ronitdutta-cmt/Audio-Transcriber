package com.audio.transcribe;

// this line is marking this a restController : (typed @RestController) .
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;


@RestController
@RequestMapping("/api/transcribe") // all the api's will be in this format .

public class TranscriptionController {

    // creating a obj
    private final OpenAiAudioTranscriptionModel transcriptionModel ; //1. creating obj

    //2. ini the obj with constructor .
    public TranscriptionController(@Value("${spring.ai.openai.api-key}") String apiKey)  //NOTE : param : (String apiKey) (OR) (@Value("${API_KEY}")) -> this will fetch the val of api key too .
    {
        OpenAiAudioApi openAiAudioApi = OpenAiAudioApi.builder() // had to paste whole thing from ChatGPT .
                .apiKey(apiKey)
                .build(); // name pasted from applcn.properties .

//        OpenAiAudioApi openAiAudioApi = new OpenAiAudioApi(apiKey) ; --> this doesn't work .


        // paste from documentation .
        this.transcriptionModel =  new OpenAiAudioTranscriptionModel(openAiAudioApi)  ; // initializing the obj here .
    }
    // read abt this again  ^


    // 3. user uploads file from this endpoint .
    @PostMapping
    public ResponseEntity<String> transcribeAudio(@RequestParam("file") MultipartFile file ) throws IOException
    {
        // this will transfer the contents of uploaded file to a newly created temporary file .
        File tempFile = File.createTempFile("audio", ".wav") ;
        file.transferTo(tempFile);

        //OpenAi understands in this format
        OpenAiAudioTranscriptionOptions transcriptionOptions = OpenAiAudioTranscriptionOptions.builder()
                .responseFormat(OpenAiAudioApi.TranscriptResponseFormat.TEXT)
                .language("en")
                .temperature(0f)
                .build();



        FileSystemResource audioFile = new FileSystemResource(tempFile); // wrapping the tempFile here .

        AudioTranscriptionPrompt transcriptionRequest = new AudioTranscriptionPrompt(audioFile, transcriptionOptions); // here model will see the audioFile , what transciption user wished to do
        AudioTranscriptionResponse response = transcriptionModel.call(transcriptionRequest); // calling the model here .

        tempFile.delete() ;

        return new ResponseEntity<>(response.getResult().getOutput(), HttpStatus.OK) ;

    }




}
