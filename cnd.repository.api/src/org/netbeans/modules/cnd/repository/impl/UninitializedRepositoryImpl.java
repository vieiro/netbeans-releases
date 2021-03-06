/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.cnd.repository.impl;

import java.util.Set;
import org.netbeans.modules.cnd.repository.api.UnitDescriptor;
import org.netbeans.modules.cnd.repository.impl.spi.LayeringSupport;
import org.netbeans.modules.cnd.repository.impl.spi.RepositoryImplementation;
import org.netbeans.modules.cnd.repository.spi.Key;
import org.netbeans.modules.cnd.repository.spi.Persistent;

/**
 *
 * @author akrasny
 */
public final class UninitializedRepositoryImpl implements RepositoryImplementation {

    private final String msg;

    public UninitializedRepositoryImpl(String msg) {
        this.msg = msg;
    }

    @Override
    public Persistent get(Key key) {
        throw new IllegalStateException(msg);
    }

    @Override
    public void put(Key key, Persistent obj) {
        throw new IllegalStateException(msg);
    }

    @Override
    public void remove(Key key) {
        throw new IllegalStateException(msg);
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void closeUnit(int unitId, boolean cleanRepository, Set<Integer> requiredUnits) {
        throw new IllegalStateException(msg);
    }

    @Override
    public void openUnit(int unitId) {
        throw new IllegalStateException(msg);
    }

    @Override
    public void removeUnit(int unitId) {
        throw new IllegalStateException(msg);
    }

    @Override
    public void hang(Key key, Persistent obj) {
        throw new IllegalStateException(msg);
    }

    @Override
    public void debugDistribution() {
        throw new IllegalStateException(msg);
    }

    @Override
    public void debugDump(Key key) {
        throw new IllegalStateException(msg);
    }

    @Override
    public int getFileIdByName(int unitId, CharSequence fileName) {
        throw new IllegalStateException(msg);
    }

    @Override
    public CharSequence getFileNameById(int unitId, int fileId) {
        throw new IllegalStateException(msg);
    }

    @Override
    public CharSequence getFileNameByIdSafe(int unitId, int fileId) {
        throw new IllegalStateException(msg);
    }

    @Override
    public CharSequence getUnitName(int unitId) {
        throw new IllegalStateException(msg);
    }

    @Override
    public int getUnitID(UnitDescriptor unitDescriptor, int storageID) {
        throw new IllegalStateException(msg);
    }

    @Override
    public int getUnitID(UnitDescriptor unitDescriptor) {
        throw new IllegalStateException(msg);
    }

    @Override
    public int getRepositoryID(int sourceUnitId) {
        throw new IllegalStateException(msg);
    }

    @Override
    public LayeringSupport getLayeringSupport(int clientUnitID) {
        return null;
    }
}
