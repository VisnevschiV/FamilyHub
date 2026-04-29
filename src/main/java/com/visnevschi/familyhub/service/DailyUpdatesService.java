package com.visnevschi.familyhub.service;

import com.visnevschi.familyhub.dbenitity.Persona;
import com.visnevschi.familyhub.document.Task;
import com.visnevschi.familyhub.document.TaskList;
import com.visnevschi.familyhub.repository.FamilyRepository;
import com.visnevschi.familyhub.repository.TaskListRepository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
public class DailyUpdatesService {
    private final FamilyRepository familyRepository;
    private final TaskListRepository taskListRepository;
    private final NotificationService notificationService;

    public DailyUpdatesService(FamilyRepository familyRepository,
                               TaskListRepository taskListRepository,
                               NotificationService notificationService) {
        this.familyRepository = familyRepository;
        this.taskListRepository = taskListRepository;
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional(readOnly = true)
    public void deleteCompletedTasks() {
        familyRepository.findAll().forEach(family -> {
            Set<Long> allMembers = family.getMembers().stream()
                    .map(Persona::getId)
                    .collect(Collectors.toSet());
            deleteCompletedTasksForFamily(family.getId(), allMembers);
        });
    }

    private void deleteCompletedTasksForFamily(Long familyId, Set<Long> allMembers) {
        List<TaskList> lists = taskListRepository.findAllByFamilyId(familyId);
        if (lists.isEmpty()) {
            return;
        }

        Map<Long, Integer> completedCountByPersona = new HashMap<>();
        List<TaskList> changedLists = new java.util.ArrayList<>();

        for (TaskList list : lists) {
            HashSet<Long> participants = list.getParticipants();
            Set<Long> recipients = (participants != null && !participants.isEmpty())
                    ? participants
                    : allMembers;

            Iterator<Task> iterator = list.getTasks().iterator();
            boolean changed = false;
            while (iterator.hasNext()) {
                Task task = iterator.next();
                if (task.isCompleted()) {
                    for (Long id : recipients) {
                        completedCountByPersona.merge(id, 1, Integer::sum);
                    }
                    iterator.remove();
                    changed = true;
                }
            }

            if (changed) {
                changedLists.add(list);
            }
        }

        if (!changedLists.isEmpty()) {
            taskListRepository.saveAll(changedLists);
        }

        completedCountByPersona.forEach((personaId, count) ->
                notificationService.createNotification(personaId,
                        count + " tasks completed today! Keep it up"));
    }
}