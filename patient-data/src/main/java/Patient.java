import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.*;
import java.util.Properties;

public class Patient {

    String[] centers= {
            "GAN",
            "SEM",
            "SHI",
            "BHA"
    };

    String givenName,familyName;
    String gender;
    int noOfPatients = 0;
    int noOfEncounter = 0;
    String urlCreatePatient = null;
    String urlCreateEncounter = null;
    private String basicAuth;

    Patient(){
        givenName = "first";
        familyName = "last";
        gender = "Female";
    }

    StringBuilder createPatientJson(int i){
        StringBuilder PatientJson=new StringBuilder();
        PatientJson.append("{\"names\":[{\"givenName\":\"");
        PatientJson.append(givenName+i);
        PatientJson.append("\",\"familyName\":\"") ;
        PatientJson.append(familyName +i);
        PatientJson.append("\"}],\"addresses\":\n" +"[{}],\n" +"\"attributes\":[],\"centerID\":{\"name\":\"");
        PatientJson.append(centers[i%4]);
        PatientJson.append("\"},\"gender\":\"");
        PatientJson.append(gender);
        PatientJson.append("\"}");

        return PatientJson;
    }

    public StringBuilder createPatientEncounterJson(int i, String patientUUID){
        String encounterDateTime="2011-02-18";
        String encounterType="REG";
        StringBuilder PatientEncounterJson=new StringBuilder();
        PatientEncounterJson.append("{\"patient\":\"");
        PatientEncounterJson.append(patientUUID);
        PatientEncounterJson.append("\",\n" + "\"encounterDatetime\":\"") ;
        PatientEncounterJson.append(encounterDateTime);
        PatientEncounterJson.append("\",\n" + "\"encounterType\":\"");
        PatientEncounterJson.append(encounterType);
        PatientEncounterJson.append("\"}");

        return PatientEncounterJson;
    }

    private String restAPICall(int i ,String url, StringBuilder Json) throws IOException {
        String output = null;
        String[] uuid=new String[13];
        try{
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(url);
            StringEntity input = new StringEntity(Json.toString());
            input.setContentType("application/json");

            postRequest.setEntity(input);
            postRequest.addHeader("Authorization", "Basic " + basicAuth);
            postRequest.addHeader("Accept","application/json");
            postRequest.addHeader("Content-Type","application/json");

            HttpResponse response = httpClient.execute(postRequest);

            if (response.getStatusLine().getStatusCode() != 201) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatusLine().getStatusCode());
            }

            BufferedReader br = new BufferedReader(
                    new InputStreamReader((response.getEntity().getContent())));

            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
                uuid=output.split("\"");
            }
            httpClient.getConnectionManager().shutdown();


        }
        catch(Exception ex){
            ex.printStackTrace();

        }
        return uuid[3];
    }

    private void createPatient() throws IOException {
        String uuid="";
        for(int i=0;i<noOfPatients;i++){
            uuid=restAPICall(i,urlCreatePatient,createPatientJson(i));
            createPatientEncounter(uuid);
        }
    }

    private void createPatientEncounter(String UUID) throws IOException {
        for(int i=0;i<noOfEncounter;i++){
            restAPICall(i,urlCreateEncounter,createPatientEncounterJson(i,UUID));
        }
    }

    public static void main(String args[]) throws IOException {
        Patient p=new Patient();
        p.readProperties();
        p.createPatient();
    }


    private void readProperties() throws FileNotFoundException {
        Properties prop=new Properties();
        try{
            prop.load(new FileInputStream("MyProp.properties"));
            noOfPatients = Integer.parseInt(prop.getProperty("noOfPatients"));
            noOfEncounter = Integer.parseInt(prop.getProperty("noOfEncounter"));
            urlCreatePatient = prop.getProperty("urlCreatePatient");
            urlCreateEncounter = prop.getProperty("urlCreateEncounter");
            String password = prop.getProperty("password");
            String username = prop.getProperty("username");
            basicAuth = new String(Base64.encodeBase64((username+":"+password).getBytes()));
        }
        catch(Exception ex){
            System.out.println(ex);
        }
    }
}
