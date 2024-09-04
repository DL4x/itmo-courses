package info.kgeorgiy.ja.shulpin.student;

import info.kgeorgiy.java.advanced.student.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.function.Function;

public class StudentDB implements StudentQuery {
    private <T> List<T> getImpl(List<Student> students,
                                Function<Student, T> function) {
        return students.stream()
                .map(function).toList();
    }

    private List<Student> sortImpl(Collection<Student> students,
                                   Comparator<? super Student> comparator) {
        return students.stream()
                .sorted(comparator).toList();
    }

    private <T> List<Student> findImpl(Collection<Student> students,
                                       Function<Student, T> function, T comp) {
        return sortStudentsByName(students.stream()
                .filter(student -> function.apply(student).equals(comp)).toList());
    }

    @Override
    public List<String> getFirstNames(List<Student> students) {
        return getImpl(students, Student::getFirstName);
    }

    @Override
    public List<String> getLastNames(List<Student> students) {
        return getImpl(students, Student::getLastName);
    }

    @Override
    public List<GroupName> getGroups(List<Student> students) {
        return getImpl(students, Student::getGroup);
    }

    @Override
    public List<String> getFullNames(List<Student> students) {
        return getImpl(students, student -> student.getFirstName() + " " + student.getLastName());
    }

    @Override
    public Set<String> getDistinctFirstNames(List<Student> students) {
        return new TreeSet<>(getFirstNames(students));
    }

    @Override
    public String getMaxStudentFirstName(List<Student> students) {
        return students.stream()
                .max(Comparator.comparing(Student::getId))
                .map(Student::getFirstName).orElse("");
    }

    @Override
    public List<Student> sortStudentsById(Collection<Student> students) {
        return sortImpl(students, Comparator.comparing(Student::getId));
    }

    @Override
    public List<Student> sortStudentsByName(Collection<Student> students) {
        return sortImpl(students,
                Comparator.comparing(Student::getLastName)
                        .thenComparing(Student::getFirstName)
                        .thenComparing(Student::getId, Comparator.reverseOrder()));
    }

    @Override
    public List<Student> findStudentsByFirstName(Collection<Student> students, String name) {
        return findImpl(students, Student::getFirstName, name);
    }

    @Override
    public List<Student> findStudentsByLastName(Collection<Student> students, String name) {
        return findImpl(students, Student::getLastName, name);
    }

    @Override
    public List<Student> findStudentsByGroup(Collection<Student> students, GroupName group) {
        return findImpl(students, Student::getGroup, group);
    }

    @Override
    public Map<String, String> findStudentNamesByGroup(Collection<Student> students, GroupName group) {
        return findStudentsByGroup(students, group).stream()
                .collect(Collectors.groupingBy(Student::getLastName,
                        Collectors.collectingAndThen(
                                Collectors.minBy(Comparator.comparing(Student::getFirstName)),
                                (Optional<Student> student) -> student.map(Student::getFirstName).orElse(""))));
    }
}
