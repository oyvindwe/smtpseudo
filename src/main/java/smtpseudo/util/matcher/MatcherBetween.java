package smtpseudo.util.matcher;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by IntelliJ IDEA.
 * User: hanishi
 * Date: 2009/06/05
 * Time: 16:34:45
 * To change this template use File | Settings | File Templates.
 */
public class MatcherBetween {
    private Entry head=new Entry(null,null,null);
    private Entry current=head;
    private CurrentMatcherState currentMatcher;
    private CurrentMatcherState reset;
    private Set<Entry> entries=new HashSet<Entry>();
    private static final Log log= LogFactory.getLog(MatcherBetween.class);
    
    public MatcherBetween(){
        head.next=head;
    }

    public void add(String name,BoyerMooreByteArrayMatcher bmBegin,BoyerMooreByteArrayMatcher bmEnd){
        log.debug(this + " adding pair: begin["+bmBegin+"],end["+bmEnd+"]");
        Entry newEntry=
                new Entry(name,new BeginMatcher(bmBegin,bmEnd),current);
        entries.add(newEntry);
        Entry tmp=newEntry.next.next;
        newEntry.next.next=newEntry;
        newEntry.next=tmp;
        current=newEntry;
        if(currentMatcher==null){
            nextMatcher();
            reset=currentMatcher;
        }
    }

    public void reset(){
        for(Entry entry:entries)
            entry.reset();
        currentMatcher=reset;
    }

    public String getName(){
        return head.getName();
    }

    public void nextMatcher(){
        do{
            currentMatcher=head.next.element;
            head=head.next;
        }while(currentMatcher==null);
    }

    int length(){
        return currentMatcher.length();
    }

    int get(int pos){
        return currentMatcher.get(pos);
    }

    int skip(int value){
        return currentMatcher.skip(value);
    }

    void swap(){
        currentMatcher.swap();
    }

    private static class Entry {
        CurrentMatcherState element;
        Entry next;
        AtomicInteger occurence=new AtomicInteger(0);
        String name;

        Entry(String name,CurrentMatcherState element, Entry next){
            this.element=element;
            this.next=next;
            this.name=name;
        }

        String getName(){
            int i;
            if(0<(i=occurence.getAndIncrement()))
                return name+i;
            return name;
        }

        void reset(){
            occurence.set(0);
        }
    }

    private static abstract class CurrentMatcherState{
        abstract int length();
        abstract int get(int pos);
        abstract int skip(int value);
        abstract void swap();
    }

    private class EndMatcher extends CurrentMatcherState {
        BoyerMooreByteArrayMatcher bmEnd;
        CurrentMatcherState nextMatcher;

        EndMatcher(BoyerMooreByteArrayMatcher bmEnd,CurrentMatcherState nextMatcher){
            this.bmEnd=bmEnd;
            this.nextMatcher=nextMatcher;
        }

        @Override
        int length() {
            return bmEnd.length();
        }

        @Override
        int get(int pos) {
            return bmEnd.get(pos);
        }

        @Override
        int skip(int value) {
            return bmEnd.skip(value);
        }

        @Override
        protected void swap() {
            currentMatcher=nextMatcher;
        }
    }

    private class BeginMatcher extends CurrentMatcherState {
        BoyerMooreByteArrayMatcher bmBegin;
        CurrentMatcherState nextMatcher;
        
        BeginMatcher(BoyerMooreByteArrayMatcher bmBegin,BoyerMooreByteArrayMatcher bmEnd){
            this.bmBegin=bmBegin;
            nextMatcher=new EndMatcher(bmEnd,this);
        }

        @Override
        int length() {
            return bmBegin.length();
        }

        @Override
        int get(int pos) {
            return bmBegin.get(pos);
        }

        @Override
        int skip(int value) {
            return bmBegin.skip(value);
        }
        
        @Override
        protected void swap() {
            currentMatcher=nextMatcher;
        }
    }
}
