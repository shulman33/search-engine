package edu.yu.cs.com1320.project.stage4.impl;

import edu.yu.cs.com1320.project.GenericCommand;
import edu.yu.cs.com1320.project.impl.StackImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

public class StackAndCommandImplTest {
    private StackImpl<GenericCommand> stack;
    private GenericCommand cmd1;
    private GenericCommand cmd2;

    @BeforeEach
    public void initVariables() throws URISyntaxException {
        this.stack = new StackImpl<GenericCommand>();
        //uri & cmd 1
        URI uri1 = new URI("http://www.test1.net");
        this.cmd1 = new GenericCommand(uri1, target ->{
            return target.equals(uri1);
        });
        //uri & cmd 2
        URI uri2 = new URI("http://www.test2.net");
        this.cmd2 = new GenericCommand(uri2, target ->{
            return target.equals(uri2);
        });
        this.stack.push(this.cmd1);
        this.stack.push(this.cmd2);
    }

    @Test
    public void pushAndPopTest(){
        GenericCommand pcmd = this.stack.pop();
        assertEquals(this.cmd2,pcmd,"first pop should've returned second GenericCommand");
        pcmd = this.stack.pop();
        assertEquals(this.cmd1,pcmd,"second pop should've returned first GenericCommand");
    }

    @Test
    public void peekTest(){
        GenericCommand pcmd = this.stack.peek();
        assertEquals(this.cmd2,pcmd,"first peek should've returned second GenericCommand");
        pcmd = this.stack.pop();
        assertEquals(this.cmd2,pcmd,"first pop after peek should've returned the second GenericCommand");

        pcmd = this.stack.peek();
        assertEquals(this.cmd1,pcmd,"second peek should've returned first GenericCommand");
        pcmd = this.stack.pop();
        assertEquals(this.cmd1,pcmd,"second pop should've returned first GenericCommand");
    }
    @Test
    public void sizeTest(){
        assertEquals(2,this.stack.size(),"two GenericCommands should be on the stack");
        this.stack.peek();
        assertEquals(2,this.stack.size(),"peek should not have affected the size of the stack");
        this.stack.pop();
        assertEquals(1,this.stack.size(),"one GenericCommand should be on the stack after one pop");
        this.stack.peek();
        assertEquals(1,this.stack.size(),"peek still should not have affected the size of the stack");
        this.stack.pop();
        assertEquals(0,this.stack.size(),"stack should be empty after 2 pops");
    }
}