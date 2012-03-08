package net.praqma.jenkins.plugin.prqa;

import hudson.FilePath;
import hudson.model.BuildListener;
import hudson.remoting.VirtualChannel;
import java.io.File;
import java.io.IOException;
import net.praqma.prqa.PRQA;
import net.praqma.prqa.products.QAC;
import net.praqma.util.debug.Logger;
import net.praqma.util.debug.appenders.StreamAppender;
import net.praqma.util.execute.AbnormalProcessTerminationException;
import net.praqma.util.execute.CmdResult;
import net.praqma.util.execute.CommandLineException;

/**
 *
 * @author Praqma
 */
public class PRQARemoteAnalysis implements FilePath.FileCallable<Boolean> {
    
    private BuildListener listener;
    private PRQA prqa;
    //private Launcher launcher;
      
    /**
     * Class representing a remote ananlysis job.  
     * 
     * @param prqa Command line application wrapper for Programming Research Applications
     * @param listener Jenkins build listener used for debugging purposes and writing to result log. 
     */
    public PRQARemoteAnalysis(PRQA prqa, BuildListener listener) {
        this.listener = listener;
        this.prqa = prqa;
    }
    
    @Override
    public Boolean invoke(File file, VirtualChannel vc) throws IOException, InterruptedException {
        listener.getLogger().println("Started analysis.");
        prqa.setCommandBase(file.getPath());
        Logger.setMinLogLevel(Logger.LogLevel.DEBUG);
        StreamAppender appender = new StreamAppender(listener.getLogger());
       
        Logger.addAppender(appender);

        try 
        {
            CmdResult res = prqa.execute();            
            if(res.stdoutList != null) {
                for(String s : res.stdoutList) {
                    listener.getLogger().println(s);
                }
            }
            if(res.errorList != null) {
                for(String error : res.stdoutList) {
                    listener.getLogger().println(error);
                }
            }
            
        } catch (AbnormalProcessTerminationException aex) {
            listener.getLogger().println(aex.getMessage());
            return false;
        } catch (CommandLineException cle) {
            listener.getLogger().println(cle.getMessage());
            return false;
        } finally  {
            Logger.removeAppender(appender);
        }
            
        return true;
    }
    
}