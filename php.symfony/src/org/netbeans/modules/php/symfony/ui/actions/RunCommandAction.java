/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.php.symfony.ui.actions;

import java.util.concurrent.Callable;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.ExecutionService;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.symfony.ui.commands.SymfonyCommandChooser;
import org.netbeans.modules.php.symfony.ui.commands.SymfonyCommandSupport;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

/**
 * @author Tomas Mysik
 */
public class RunCommandAction extends CallableSystemAction {
    private static final long serialVersionUID = -22735302227232842L;

    @Override
    public void performAction() {
        PhpModule phpModule = PhpModule.inferPhpModule();
        if (phpModule == null) {
            return;
        }

        SymfonyCommandChooser.CommandDescriptor commandDescriptor = SymfonyCommandChooser.select(phpModule);
        if (commandDescriptor == null) {
            return;
        }

        String displayName = phpModule.getDisplayName() + " (" + commandDescriptor.getSymfonyCommand().getCommand() + ")"; // NOI18N
        final String[] params;
        // FIXME all parameters in one String should we split it ?
        if (commandDescriptor.getCommandParams() != null && !"".equals(commandDescriptor.getCommandParams().trim())) { // NOI18N
            params = new String[] {commandDescriptor.getCommandParams()};
        } else {
            params = new String[] {};
        }


        Callable<Process> callable;
        ExecutionDescriptor descriptor;

        callable = SymfonyCommandSupport.getForPhpModule(phpModule).createCommand(
                commandDescriptor.getSymfonyCommand().getCommand(), phpModule, params);
        descriptor = SymfonyCommandSupport.getForPhpModule(phpModule).getDescriptor(commandDescriptor.getSymfonyCommand().getCommand());
        ExecutionService service = ExecutionService.newService(callable, descriptor, displayName);
        service.run();
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(RunCommandAction.class, "LBL_RunCommand");
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}
