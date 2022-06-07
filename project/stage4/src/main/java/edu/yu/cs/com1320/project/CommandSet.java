package edu.yu.cs.com1320.project;

import java.util.*;

public class CommandSet<Target> extends AbstractSet<GenericCommand<Target>> implements Undoable {

    private HashSet<GenericCommand<Target>> genericCommands;

    public CommandSet(){
        this.genericCommands = new HashSet<GenericCommand<Target>>();
    }

    /**
     * does this CommandSet include a command whose target is c?
     * @param c
     * @return
     */
    public boolean containsTarget(Target c) {
        return this.genericCommands.contains(new GenericCommand<>(c,null));
    }

    /**
     * Add a command to this command set.
     * A single Target can only have ONE command in the set.
     * @param genericCommand
     * @throws IllegalArgumentException if this set already contains a command for this Target
     */
    public void addCommand(GenericCommand<Target> genericCommand){
        if(containsTarget(genericCommand.getTarget())){
            throw new IllegalArgumentException("this CommandSet already has a command for " + genericCommand.getTarget().toString());
        }
        this.genericCommands.add(genericCommand);
    }

    /**
     *
     * @param c the target to undo
     */
    public boolean undo(Target c){
        if(containsTarget(c)){
            GenericCommand<Target> todo = null;
            for(GenericCommand<Target> cmd : this.genericCommands){
                if(cmd.getTarget().equals(c)){
                    todo = cmd;
                    break;
                }
            }
            if(todo != null){
                this.genericCommands.remove(todo);
                return todo.undo();

            }
        }
        return false;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean undo() {
        int size = this.genericCommands.size();
        return this.undoAll().size() == size;
    }
    /**
     * undo all the commands in this command set.
     * all undone commands are removed from the command set.
     * @return a set of the undone commands
     */
    public Set<GenericCommand<Target>> undoAll(){
        HashSet<GenericCommand<Target>> undone = new HashSet<>(this.genericCommands.size());
        Object[] allCommands = this.genericCommands.toArray();
        for(Object cmdObj : allCommands){
            GenericCommand<Target> cmd = (GenericCommand<Target>)cmdObj;
            if(cmd.undo()){
                undone.add(cmd);
                this.genericCommands.remove(cmd);
            }
        }
        return undone;
    }

    @Override
    public Iterator<GenericCommand<Target>> iterator() {
        return this.genericCommands.iterator();
    }

    @Override
    public int size() {
        return this.genericCommands.size();
    }
}