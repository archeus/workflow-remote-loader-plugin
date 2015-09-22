/*
 * The MIT License
 *
 * Copyright (c) 2015 CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jenkinsci.plugins.workflow.lib_manager;

import groovy.lang.Binding;
import hudson.Extension;
import java.io.IOException;
import org.jenkinsci.plugins.scriptsecurity.sandbox.whitelists.ProxyWhitelist;
import org.jenkinsci.plugins.scriptsecurity.sandbox.whitelists.StaticWhitelist;
import org.jenkinsci.plugins.workflow.cps.CpsScript;
import org.jenkinsci.plugins.workflow.cps.GlobalVariable;

/**
 *
 * @author Oleg Nenashev
 */
@Extension
public class PluginDSL extends GlobalVariable {
    
    @Override 
    public String getName() {
        return "libManager";
    }

    @Override 
    public Object getValue(CpsScript script) throws Exception {
        Binding binding = script.getBinding();
        Object libManager;
        if (binding.hasVariable(getName())) {
            libManager = binding.getVariable(getName());
        } else {
            // Note that if this were a method rather than a constructor, we would need to mark it @NonCPS lest it throw CpsCallableInvocation.
            libManager = script.getClass().getClassLoader().loadClass("org.jenkinsci.plugins.workflow.lib_manager.LibManager").getConstructor(CpsScript.class).newInstance(script);
            binding.setVariable(getName(), libManager);
        }
        return libManager;
    }

    @Extension public static class MiscWhitelist extends ProxyWhitelist {
        public MiscWhitelist() throws IOException {
            super(new StaticWhitelist(
                    "method groovy.lang.Closure call java.lang.Object",
                    "method java.lang.Object toString",
                    "method groovy.lang.GroovyObject invokeMethod java.lang.String java.lang.Object"
            ));
        }
    }
}
