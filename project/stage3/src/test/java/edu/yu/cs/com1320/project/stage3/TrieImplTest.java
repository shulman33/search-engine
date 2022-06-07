package edu.yu.cs.com1320.project.stage3.impl;

import edu.yu.cs.com1320.project.impl.TrieImpl;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TrieImplTest {

    private TrieImpl<Integer> getTestTrie(){
        TrieImpl<Integer> ti = new TrieImpl<>();
        ti.put("oNe",11);
        ti.put("onE",1);
        ti.put("one",111);
        ti.put("oneANdDone",121);
        ti.put("OneAndDOne",131);
        ti.put("oneAndDonE",101);
        ti.put("tWo",2);
        ti.put("twO",22);
        ti.put("twoAndMOre",23456);
        ti.put("twoAndMoRe",27895);
        return ti;
    }

    @Test
    public void testDelete(){
        TrieImpl<Integer> ti = getTestTrie();
        int val = ti.delete("one",11);
        assertEquals(val,11,"delete(\"one\",11) should returned 11");
        List<Integer> ones = ti.getAllSorted("one",getComparator());
        assertEquals(2,ones.size(),"getAllSorted(\"one\")should've returned 2 results");
        assertEquals(111,ones.get(0));
        assertEquals(1,ones.get(1));
    }

    @Test
    public void testDeleteAll(){
        TrieImpl<Integer> ti = getTestTrie();
        //make sure that those which were supposed to be deleted were
        Set<Integer> oneanddone = ti.deleteAll("oneanddone");
        assertEquals(3,oneanddone.size(),"deleteAll(\"oneanddone\")should've returned 3 results");
        assertTrue(oneanddone.contains(131));
        assertTrue(oneanddone.contains(121));
        assertTrue(oneanddone.contains(101));
        //check that others are still present
        List<Integer> ones = ti.getAllWithPrefixSorted("one",getComparator());
        assertEquals(3,ones.size(),"getAllWithPrefixSorted(\"one\")should've returned 3 results");
        assertEquals(111,ones.get(0));
        assertEquals(11,ones.get(1));
        assertEquals(1,ones.get(2));
        List<Integer> twos = ti.getAllWithPrefixSorted("two",getComparator());
        assertEquals(4,twos.size(),"getAllWithPrefixSorted(\"two\")should've returned 4 results");
        assertEquals(27895,twos.get(0));
        assertEquals(23456,twos.get(1));
        assertEquals(22,twos.get(2));
        assertEquals(2,twos.get(3));
    }

    @Test
    public void testGetAllWithPrefixSorted(){
        TrieImpl<Integer> ti = getTestTrie();
        List<Integer> ones = ti.getAllWithPrefixSorted("one",getComparator());
        //one
        assertEquals(6,ones.size(),"getAllWithPrefixSorted(\"one\")should've returned 6 results");
        assertEquals(131,ones.get(0));
        assertEquals(121,ones.get(1));
        assertEquals(111,ones.get(2));
        assertEquals(101,ones.get(3));
        assertEquals(11,ones.get(4));
        assertEquals(1,ones.get(5));
        //two
        List<Integer> two = ti.getAllWithPrefixSorted("TwO",getComparator());
        assertEquals(4,two.size(),"getAllWithPrefixSorted(\"TwO\")should've returned 4 results");
        assertEquals(27895,two.get(0));
        assertEquals(23456,two.get(1));
        assertEquals(22,two.get(2));
        assertEquals(2,two.get(3));
    }

    @Test
    public void testDeleteAllWithPrefix(){
        TrieImpl<Integer> ti = getTestTrie();
        Set<Integer> two = ti.deleteAllWithPrefix("two");
        assertEquals(4,two.size(),"deleteAllWithPrefix(\"TwO\")should've returned 4 results");
        assertTrue(two.contains(27895));
        assertTrue(two.contains(23456));
        assertTrue(two.contains(22));
        assertTrue(two.contains(2));
    }

    @Test
    public void testPutAndGetAll(){
        TrieImpl<Integer> ti = new TrieImpl<>();
        ti.put("one",11);
        ti.put("one",121);
        ti.put("two",2);
        List<Integer> ones = ti.getAllSorted("one",this.getComparator());
        assertEquals(2,ones.size(),"getAllSorted should've returned 2 results");
        assertEquals(121,ones.get(0),"first element should be 121");
        assertEquals(11,ones.get(1),"second element should be 11");
    }

    private Comparator<Integer> getComparator(){
        return new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                if(o1 < o2){
                    return 1;
                }else if(o1.equals(o2)){
                    return 0;
                }else{
                    return -1;
                }
            }
        };
    }
}