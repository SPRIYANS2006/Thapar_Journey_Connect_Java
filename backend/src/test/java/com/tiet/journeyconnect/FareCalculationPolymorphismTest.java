package com.tiet.journeyconnect;

import com.tiet.journeyconnect.factory.TravelGroupFactory;
import com.tiet.journeyconnect.factory.TravelGroupType;
import com.tiet.journeyconnect.models.CabRideGroup;
import com.tiet.journeyconnect.models.RideMember;
import com.tiet.journeyconnect.models.TrainTravelGroup;
import com.tiet.journeyconnect.models.BusTravelGroup;
import com.tiet.journeyconnect.models.TravelGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 Test Suite demonstrating Run-Time Polymorphism / Dynamic Method Dispatch
 * in accordance with University Java Lab Evaluation criteria.
 */
public class FareCalculationPolymorphismTest {

    @Test
    @DisplayName("Polymorphism: CabRideGroup splits total fare equally among occupied seats")
    void testCabRideGroupFareSplitting() {
        CabRideGroup cab = new CabRideGroup("1", "del_airport", "Delhi Airport", "2026-11-19", "10:00", "Shared Cab", 4, "student_1");

        // Rider 1 joins
        cab.addMember(new RideMember("student_1", "Aarav Sharma", "Tejas Hall", "9876543210", "10:00", "Group Admin"));
        assertEquals(1400.0, cab.calculatePerRiderFare(1400.0), 0.01, "Solo rider pays full ₹1400 fare");

        // Rider 2 joins
        cab.addMember(new RideMember("student_2", "Diya Verma", "Vasudha Hall", "9876543211", "10:00", "Member"));
        assertEquals(700.0, cab.calculatePerRiderFare(1400.0), 0.01, "2 riders split ₹1400 as ₹700 each");

        // Rider 3 joins
        cab.addMember(new RideMember("student_3", "Rohan Mehta", "Prithvi Hall", "9876543212", "10:00", "Member"));
        assertEquals(466.67, cab.calculatePerRiderFare(1400.0), 0.01, "3 riders split ₹1400 as ₹466.67 each");

        // Rider 4 joins (Cab full at 4 riders)
        cab.addMember(new RideMember("student_4", "Ananya Singh", "Ira Hall", "9876543213", "10:00", "Member"));
        assertEquals(350.0, cab.calculatePerRiderFare(1400.0), 0.01, "4 riders split ₹1400 as ₹350 each");
    }

    @Test
    @DisplayName("Polymorphism: TrainTravelGroup has zero ticket liability (self-booking model)")
    void testTrainTravelGroupZeroLiability() {
        TrainTravelGroup train = new TrainTravelGroup(
                "tg_1", "Shatabdi Express Cohort", "del", "Delhi NCR",
                "2026-11-19", "07:00", "Patiala Railway Station", "New Delhi",
                "12005", "CC", "student_1"
        );

        train.addMember(new RideMember("student_1", "Aarav", "Tejas", "9876543210", "07:00", "Admin"));
        train.addMember(new RideMember("student_2", "Diya", "Vasudha", "9876543211", "07:00", "Member"));
        train.addMember(new RideMember("student_3", "Rohan", "Prithvi", "9876543212", "07:00", "Member"));

        // Train cohorts operate on self-booking model; platform does not pool ticket payments
        assertEquals(0.0, train.calculatePerRiderFare(500.0), "Train cohort per-rider fare liability is 0.0");
    }

    @Test
    @DisplayName("Polymorphism: BusTravelGroup has zero ticket liability (self-booking model)")
    void testBusTravelGroupZeroLiability() {
        BusTravelGroup bus = new BusTravelGroup(
                "tg_2", "PRTC Volvo to ISBT 43", "chd", "Chandigarh & Tri-City",
                "2026-11-19", "08:30", "Patiala Bus Stand", "Chandigarh Sector 43",
                "PRTC Volvo", "student_1"
        );

        bus.addMember(new RideMember("student_1", "Aarav", "Tejas", "9876543210", "08:30", "Admin"));
        bus.addMember(new RideMember("student_2", "Diya", "Vasudha", "9876543211", "08:30", "Member"));

        assertEquals(0.0, bus.calculatePerRiderFare(120.0), "Bus cohort per-rider fare liability is 0.0");
    }

    @Test
    @DisplayName("Polymorphic invocation through base abstract class reference TravelGroup")
    void testPolymorphicBaseReferenceDispatch() {
        TravelGroupFactory.GroupSpecification spec1 = new TravelGroupFactory.GroupSpecification();
        spec1.setId("cab_1");
        spec1.setDestination("Delhi Airport");
        spec1.setDate("2026-11-20");
        spec1.setDepartureTime("14:00");
        spec1.setMaxMembers(4);

        TravelGroup cabGroup = TravelGroupFactory.createGroup(TravelGroupType.CAB, spec1, "s1", "Aarav", "HJ", "9876543210");
        cabGroup.addMember(new RideMember("s2", "Diya", "HE", "9876543211", "14:00", "Member"));

        TravelGroupFactory.GroupSpecification spec2 = new TravelGroupFactory.GroupSpecification();
        spec2.setId("train_1");
        spec2.setTitle("Kalka Shatabdi");
        spec2.setDestination("Chandigarh");
        spec2.setDate("2026-11-20");
        spec2.setDepartureTime("18:00");
        spec2.setTrainNumber("12011");

        TravelGroup trainGroup = TravelGroupFactory.createGroup(TravelGroupType.TRAIN, spec2, "s3", "Rohan", "HB", "9876543212");

        // Invoke polymorphic calculatePerRiderFare on abstract TravelGroup reference
        assertEquals(700.0, cabGroup.calculatePerRiderFare(1400.0), 0.01);
        assertEquals(0.0, trainGroup.calculatePerRiderFare(1400.0), 0.01);
    }
}
