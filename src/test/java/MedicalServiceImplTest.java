import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoFileRepository;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;
import ru.netology.patient.service.alert.SendAlertServiceImpl;
import ru.netology.patient.service.medical.MedicalService;
import ru.netology.patient.service.medical.MedicalServiceImpl;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MedicalServiceImplTest {
    PrintStream standardOut = System.out;
    ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {

        System.setOut(new PrintStream(outputStreamCaptor));
    }
    @AfterEach
    public void tearDown() {
        System.setOut(standardOut);
    }
    PatientInfo patient1 = new PatientInfo ("255","Иван", "Петров", LocalDate.of(1980, 11, 26),
            new HealthInfo(new BigDecimal("36.6"), new BloodPressure(120, 80)));

    @Test
    @DisplayName("1. Вывод сообщения во время проверки давления")
    public void testMessageCheckBloodPressure () {
        //Arrange
        PatientInfoRepository mockInfoPatients = Mockito.mock(PatientInfoFileRepository.class);
        String id = mockInfoPatients.add (patient1);
        Mockito.when(mockInfoPatients.getById(id)).thenReturn(patient1);
        Mockito.when(mockInfoPatients.add(patient1)).thenReturn(id);

        SendAlertService alertService = new SendAlertServiceImpl();
        MedicalService serviceMed = new MedicalServiceImpl(mockInfoPatients, alertService);

        BloodPressure badPressure = new BloodPressure(60, 120);
        String expected = "Warning, patient with id: 255, need help";
        //Act
        serviceMed.checkBloodPressure(id, badPressure);
        //Assert
        assertEquals(outputStreamCaptor.toString().trim(), expected);
    }

    @Test
    @DisplayName("2. Вывод сообщения во время проверки температуры")
    public void testMessageCheckTemperature () {
        //Arrange
        PatientInfoRepository mockInfoPatients = Mockito.mock(PatientInfoFileRepository.class);
        String id = "255";
        Mockito.when(mockInfoPatients.getById(id)).thenReturn(patient1);
        Mockito.when(mockInfoPatients.add(patient1)).thenReturn(id);

        SendAlertService alertService = new SendAlertServiceImpl();
        MedicalService serviceMed = new MedicalServiceImpl(mockInfoPatients, alertService);

        BigDecimal badTemperature = new BigDecimal("35.0");
        String expected = "Warning, patient with id: 255, need helpWarning, patient with id: 255, need help";
        //Act
        serviceMed.checkTemperature(id, badTemperature);
        //Assert
        assertEquals(outputStreamCaptor.toString().trim(), expected);
    }

    @Test
    @DisplayName("3. Сообщение не выводится, когда давление в норме")
    public void testNoMessageCheckBloodPressure () {
        //Arrange
        PatientInfoRepository mockInfoPatients = Mockito.mock(PatientInfoFileRepository.class);
        String id = mockInfoPatients.add (patient1);
        Mockito.when(mockInfoPatients.getById(id)).thenReturn(patient1);
        Mockito.when(mockInfoPatients.add(patient1)).thenReturn(id);

        SendAlertService alertService = new SendAlertServiceImpl();
        MedicalService serviceMed = new MedicalServiceImpl(mockInfoPatients, alertService);

        BloodPressure normalPressure = new BloodPressure(120, 80);
        String expected = "";
        //Act
        serviceMed.checkBloodPressure(id, normalPressure);
        //Assert
        assertEquals(outputStreamCaptor.toString().trim(), expected);
    }

    @Test
    @DisplayName("4. Сообщение не выводится, когда температура в норме")
    public void testNoMessageCheckTemperature () {
        //Arrange
        PatientInfoRepository mockInfoPatients = Mockito.mock(PatientInfoFileRepository.class);
        String id = "255";
        Mockito.when(mockInfoPatients.getById(id)).thenReturn(patient1);
        Mockito.when(mockInfoPatients.add(patient1)).thenReturn(id);

        SendAlertService alertService = new SendAlertServiceImpl();
        MedicalService serviceMed = new MedicalServiceImpl(mockInfoPatients, alertService);

        BigDecimal normalTemperature = new BigDecimal("36.1");
        String expected = "";
        //Act
        serviceMed.checkTemperature(id, normalTemperature);
        //Assert
        assertEquals(outputStreamCaptor.toString().trim(), expected);
    }
}
