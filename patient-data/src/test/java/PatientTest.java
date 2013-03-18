import junit.framework.Assert;
import org.junit.Test;


public class PatientTest {

    @Test
    public void shouldReturnPatientJsonInCorrectFormat(){
        Patient patient=new Patient();
        StringBuilder stExpect=patient.createPatientJson(1);
        StringBuilder stActual =new StringBuilder();
        stActual.append("{\"names\":[{\"givenName\":\"first1\",\"familyName\":\"last1\"}],\"addresses\":\n" +
                "[{}],\n" +
                "\"attributes\":[],\"centerID\":{\"name\":\"SEM\"},\"gender\":\"Female\"}");
        Assert.assertEquals(stActual.toString(),stExpect.toString());
    }

    @Test
    public void shouldReturnPatientEncounterJsonInCorrectFormat(){
        Patient patient=new Patient();
        StringBuilder stExpect=patient.createPatientEncounterJson(1,"d03bc81c-60ce-4609-a0cb-946d3e402e65");
        StringBuilder stActual =new StringBuilder();
        stActual.append("{\"patient\":\"d03bc81c-60ce-4609-a0cb-946d3e402e65\",\n" +
                "\"encounterDatetime\":\"2011-02-18\",\n" +
                "\"encounterType\":\"REG\"}");
        Assert.assertEquals(stActual.toString(),stExpect.toString());
    }



}
