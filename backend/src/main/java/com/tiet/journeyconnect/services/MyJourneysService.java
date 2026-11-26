package com.tiet.journeyconnect.services;

import com.tiet.journeyconnect.models.entities.*;
import com.tiet.journeyconnect.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class MyJourneysService {

    private static final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private CityRideRepository cityRideRepository;

    @Autowired
    private TravelGroupRepository travelGroupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private SerializerService serializerService;

    public Map<String, Object> getMyJourneysFeed(UserEntity user) {
        List<GroupMemberEntity> memberships = groupMemberRepository.findByUserId(user.getId());

        List<Map<String, Object>> allJourneys = new ArrayList<>();
        Set<String> seenIds = new HashSet<>();

        for (GroupMemberEntity mem : memberships) {
            String targetId = mem.getTargetId();
            if (seenIds.contains(targetId)) continue;
            seenIds.add(targetId);

            if ("CAMPUS_GROUP".equals(mem.getGroupType())) {
                Optional<GroupEntity> gOpt = groupRepository.findById(targetId);
                if (gOpt.isPresent()) {
                    GroupEntity g = gOpt.get();
                    List<GroupMemberEntity> members = groupMemberRepository.findByTargetIdAndGroupTypeOrderByJoinedAtAsc(g.getId(), "CAMPUS_GROUP");
                    Map<String, Object> map = serializerService.serializeGroup(g, members, user.getId());
                    map.put("kind", "ride");
                    map.put("mode", "Shared Auto".equalsIgnoreCase(g.getTransport()) ? "Auto" : "Cab");
                    map.put("destination", g.getDestinationName());
                    allJourneys.add(map);
                }
            } else if ("CITY_RIDE".equals(mem.getGroupType())) {
                Optional<CityRideEntity> rOpt = cityRideRepository.findById(targetId);
                if (rOpt.isPresent()) {
                    CityRideEntity r = rOpt.get();
                    List<GroupMemberEntity> members = groupMemberRepository.findByTargetIdAndGroupTypeOrderByJoinedAtAsc(r.getId(), "CITY_RIDE");
                    Map<String, Object> map = serializerService.serializeCityRide(r, members, user.getId());
                    map.put("kind", "ride");
                    map.put("isCommunityRide", true);
                    map.put("mode", r.getMode());
                    map.put("departureTime", r.getTime());
                    map.put("destinationName", r.getDestination());
                    map.put("maxMembers", r.getMaxCapacity());
                    map.put("adminId", r.getCreatorId());
                    map.put("transport", "Cab".equalsIgnoreCase(r.getMode()) ? "Shared Cab" : r.getMode());
                    allJourneys.add(map);
                }
            } else if ("TRAVEL_GROUP".equals(mem.getGroupType())) {
                Optional<TravelGroupEntity> tgOpt = travelGroupRepository.findById(targetId);
                if (tgOpt.isPresent()) {
                    TravelGroupEntity tg = tgOpt.get();
                    List<GroupMemberEntity> members = groupMemberRepository.findByTargetIdAndGroupTypeOrderByJoinedAtAsc(tg.getId(), "TRAVEL_GROUP");
                    Map<String, Object> map = serializerService.serializeTravelGroup(tg, members, user.getId());
                    map.put("kind", "travel");
                    map.put("mode", tg.getType());
                    map.put("origin", tg.getBoardingPoint());
                    map.put("destinationName", tg.getDestination());
                    allJourneys.add(map);
                }
            }
        }

        List<Map<String, Object>> upcoming = new ArrayList<>();
        List<Map<String, Object>> past = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now(IST_ZONE);

        for (Map<String, Object> j : allJourneys) {
            String date = (String) j.get("date");
            String time = (String) j.get("departureTime");
            String status = (String) j.get("status");

            double hours = SlotEngineService.getHoursUntilDeparture(date, time, now);
            boolean isPast = "Completed".equalsIgnoreCase(status) || "Cancelled".equalsIgnoreCase(status) || hours <= 0;

            if (isPast) {
                past.add(j);
            } else {
                upcoming.add(j);
            }
        }

        Comparator<Map<String, Object>> chronological = (a, b) -> {
            String dtA = a.get("date") + "T" + a.get("departureTime");
            String dtB = b.get("date") + "T" + b.get("departureTime");
            int cmp = dtA.compareTo(dtB);
            if (cmp != 0) return cmp;
            return String.valueOf(a.get("id")).compareTo(String.valueOf(b.get("id")));
        };

        upcoming.sort(chronological);
        past.sort((a, b) -> chronological.compare(b, a));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("upcoming", upcoming);
        result.put("past", past);
        return result;
    }
}
