import { useState } from "react";
import axios from 'axios';

const AudioUploader = () =>{

    const[file,setFile] = useState(null) ; 
    const[transcription,setTranscription] = useState("") ; // for output 

    const handleFileChange = (e) =>{
        setFile(e.target.files[0])  ;  // will give the file that user has selected. 
    }; 

      //now we need to upload(means triggering backend ) the file & trigger the API .-> use of axios .

    const handleUpload= async() =>{
        const formData = new FormData() ; 
        formData.append('file',file) ; 

        try {
            const response = await axios.post('http://localhost:8080/api/trancribe',formData,{
                headers :{
                    'Content-type' : 'multipart/form-data' , 
                }
            });
            setTranscription(response.data) ; 

        } catch (error) {
            console.error("Error transribing audio" , error ) ;  
        }        
    } ;

    return(
        <div className="container">
            <h1> Audio to Text Transcriber </h1>

            <div className="file-input">
                <input type="file" accept="audio/*"  onChange={handleFileChange}/>
            </div>
            <button className="upload-button" onClick={handleUpload}>Upload and transcribe</button>
            <div className="transcription-result">
                <h2>Transcription Result </h2>
                <p>{transcription}</p>
            </div>
        </div>
    );
}

export default AudioUploader