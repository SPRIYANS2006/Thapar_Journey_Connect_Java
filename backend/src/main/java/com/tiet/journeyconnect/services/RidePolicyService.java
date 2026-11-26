package com.tiet.journeyconnect.services;

import com.tiet.journeyconnect.exceptions.SingleRidePolicyViolationException;
import com.tiet.journeyconnect.models.entities.*;
import com.tiet.journeyconnect.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service enforcing the campus-wide Single-Ride Policy.
 * In accordance with institutional safety and group accountability guidelines,
 * a student cannot simultaneously hold seats in multiple active rides across
 * Campus Dashboard slots (CAMPUS_GROUP), City Cab rides (CITY_RIDE), and Intercity Travel Groups (TRAVEL_GROUP).
 */
@Service
public class RidePolicyService {

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private CityRideRepository cityRideRepository;

    @Autowired
    private TravelGroupRepository travelGroupRepository;

    /**
     * Validates that the specified user does not belong to any other active ride.
     *
     * @param userId The ID of the student attempting to create or join a ride.
     * @param targetGroupId The ID of the group currently being joined (or null if creating a new group).
     *                      If the student is already in this exact group, it is allowed so callers can handle idempotency.
     * @throws SingleRidePolicyViolationException if the user is already enrolled in another active group.
     */
    public void validateSingleRidePolicy(String userId, String targetGroupId) {
        if (userId == null || userId.trim().isEmpty()) {
            return;
        }

        List<GroupMemberEntity> userMemberships = groupMemberRepository.findByUserId(userId);
        for (GroupMemberEntity mem : userMemberships) {
            String targetId = mem.getTargetId();
            if (targetGroupId != null && targetId.equals(targetGroupId)) {
                // Same group - allow member to proceed
                continue;
            }

            if ("CAMPUS_GROUP".equals(mem.getGroupType())) {
                Optional<GroupEntity> gOpt = groupRepository.findById(targetId);
                if (gOpt.isPresent()) {
                    GroupEntity g = gOpt.get();
                    if ("Open".equalsIgnoreCase(g.getStatus()) || "Confirmed".equalsIgnoreCase(g.getStatus())) {
                        throw new SingleRidePolicyViolationException(
                                "Single-Ride Policy: You already have an active Campus Ride (Group #" + g.getId() + " to "
                                        + g.getDestinationName() + " at " + g.getDepartureTime()
                                        + "). You cannot join or create multiple rides simultaneously across campus and community. Please leave or reschedule your existing ride first."
                        );
                    }
                }
            } else if ("CITY_RIDE".equals(mem.getGroupType())) {
                Optional<CityRideEntity> rOpt = cityRideRepository.findById(targetId);
                if (rOpt.isPresent()) {
                    CityRideEntity r = rOpt.get();
                    if ("Open".equalsIgnoreCase(r.getStatus()) || "Full".equalsIgnoreCase(r.getStatus()) || "Confirmed".equalsIgnoreCase(r.getStatus())) {
                        throw new SingleRidePolicyViolationException(
                                "Single-Ride Policy: You already have an active City Cab Ride to " + r.getDestination()
                                        + " (" + r.getCommunityName() + " at " + r.getTime()
                                        + "). You cannot join or create multiple rides simultaneously across campus and community. Please leave your existing ride first."
                        );
                    }
                }
            } else if ("TRAVEL_GROUP".equals(mem.getGroupType())) {
                Optional<TravelGroupEntity> tgOpt = travelGroupRepository.findById(targetId);
                if (tgOpt.isPresent()) {
                    TravelGroupEntity tg = tgOpt.get();
                    if ("Open".equalsIgnoreCase(tg.getStatus()) || "Confirmed".equalsIgnoreCase(tg.getStatus())) {
                        throw new SingleRidePolicyViolationException(
                                "Single-Ride Policy: You already have an active Travel Group (\"" + tg.getTitle()
                                        + "\" to " + tg.getDestination() + " at " + tg.getDepartureTime()
                                        + "). You cannot join or create multiple rides simultaneously across campus and community. Please leave your existing travel group first."
                        );
                    }
                }
            }
        }
    }
}
