package name.ncg777.maths.enumerations;

import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.*;

public class PartitionEnumerationTests {

    private List<List<Integer>> collectPartitions(PartitionEnumeration enumeration) {
        List<List<Integer>> partitions = new ArrayList<>();
        while (enumeration.hasMoreElements()) {
            partitions.add(enumeration.nextElement());
        }
        return partitions;
    }

    @Test
    public void testPartitionsFor1() {
        PartitionEnumeration enumeration = PartitionEnumeration.of(1);
        List<List<Integer>> partitions = collectPartitions(enumeration);
        
        assertThat(partitions, hasSize(1));
        assertThat(partitions.get(0), is(equalTo(Collections.singletonList(1))));
    }

    @Test
    public void testPartitionsFor2() {
        PartitionEnumeration enumeration = PartitionEnumeration.of(2);
        List<List<Integer>> partitions = collectPartitions(enumeration);
        
        assertThat(partitions, hasSize(2));
        assertThat(partitions.get(1), is(equalTo(Arrays.asList(2))));
        assertThat(partitions.get(0), is(equalTo(Arrays.asList(1, 1))));
    }

    @Test
    public void testPartitionsFor3() {
        PartitionEnumeration enumeration = PartitionEnumeration.of(3);
        List<List<Integer>> partitions = collectPartitions(enumeration);
        
        assertThat(partitions, hasSize(3));
        assertThat(partitions.get(2), is(equalTo(Arrays.asList(3))));
        assertThat(partitions.get(1), is(equalTo(Arrays.asList(2, 1))));
        assertThat(partitions.get(0), is(equalTo(Arrays.asList(1, 1, 1))));
    }

    @Test
    public void testPartitionsFor4() {
        PartitionEnumeration enumeration = PartitionEnumeration.of(4);
        List<List<Integer>> partitions = collectPartitions(enumeration);
        
        assertThat(partitions, hasSize(5));
        assertThat(partitions.get(4), is(equalTo(Arrays.asList(4))));
        assertThat(partitions.get(3), is(equalTo(Arrays.asList(3, 1))));
        assertThat(partitions.get(2), is(equalTo(Arrays.asList(2, 2))));
        assertThat(partitions.get(1), is(equalTo(Arrays.asList(2, 1, 1))));
        assertThat(partitions.get(0), is(equalTo(Arrays.asList(1, 1, 1, 1))));
    }
    
    @Test
    public void testPartitionsFor5() {
        PartitionEnumeration enumeration = PartitionEnumeration.of(5);
        List<List<Integer>> partitions = collectPartitions(enumeration);

        assertThat(partitions, hasSize(7));
        assertThat(partitions.get(6), is(equalTo(Arrays.asList(5))));
        assertThat(partitions.get(5), is(equalTo(Arrays.asList(4, 1))));
        assertThat(partitions.get(4), is(equalTo(Arrays.asList(3, 2))));
        assertThat(partitions.get(3), is(equalTo(Arrays.asList(3, 1, 1))));
        assertThat(partitions.get(2), is(equalTo(Arrays.asList(2, 2, 1))));
        assertThat(partitions.get(1), is(equalTo(Arrays.asList(2, 1, 1, 1))));
        assertThat(partitions.get(0), is(equalTo(Arrays.asList(1, 1, 1, 1, 1))));
    }
}

