#!/bin/sh
# 
# DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
# 
# Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
# 
# The contents of this file are subject to the terms of either the GNU General Public
# License Version 2 only ("GPL") or the Common Development and Distribution
# License("CDDL") (collectively, the "License"). You may not use this file except in
# compliance with the License. You can obtain a copy of the License at
# http://www.netbeans.org/cddl-gplv2.html or nbbuild/licenses/CDDL-GPL-2-CP. See the
# License for the specific language governing permissions and limitations under the
# License.  When distributing the software, include this License Header Notice in
# each file and include the License file at nbbuild/licenses/CDDL-GPL-2-CP.  Sun
# designates this particular file as subject to the "Classpath" exception as provided
# by Sun in the GPL Version 2 section of the License file that accompanied this code.
# If applicable, add the following below the License Header, with the fields enclosed
# by brackets [] replaced by your own identifying information:
# "Portions Copyrighted [year] [name of copyright owner]"
# 
# Contributor(s):
# 
# The Original Software is NetBeans. The Initial Developer of the Original Software
# is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun Microsystems, Inc. All
# Rights Reserved.
# 
# If you wish your version of this file to be governed by only the CDDL or only the
# GPL Version 2, indicate your decision by adding "[Contributor] elects to include
# this software in this distribution under the [CDDL or GPL Version 2] license." If
# you do not indicate a single choice of license, a recipient has the option to
# distribute your version of this file under either the CDDL, the GPL Version 2 or
# to extend the choice of license to its licensees as provided above. However, if you
# add GPL Version 2 code and therefore, elected the GPL Version 2 license, then the
# option applies only if the new code is made subject to such option by the copyright
# holder.
# 

set -x
ISROOT="$1"
SYSTEM_TYPE="$2"
PASSWORD="$3"

INSTALLDIR=`pwd`

cp ./support-files/my-"$SYSTEM_TYPE".cnf ./my.cnf


do_query() {
    tmpFile=./query.tmp
    echo "$1" > $tmpFile
    if [ 1 -eq $ISROOT ] ; then
        ./bin/mysql --defaults-file=./my.cnf <$tmpFile
    else
        if [ -n "$PASSWORD" ] ; then
            ./bin/mysql --defaults-file=./my.cnf --user=root --password="$PASSWORD" <$tmpFile
        else
            ./bin/mysql --defaults-file=./my.cnf --user=root <$tmpFile
	fi
    fi
    code=$?
    rm $tmpFile
    return $code
}
escape() {
	echo "$1" | sed -e "s/\//\\\\\//g"
}


remove_remote_root() {
    do_query "DELETE FROM mysql.user WHERE User='root' AND Host!='localhost';"
    if [ $? -eq 0 ] ; then
	echo " ... Success!"
    else
	echo " ... Failed!"
    fi
}

remove_anonymous() {
    if [ -n "$REMOVE_ANONYMOUS" ] ; then 
        do_query "DELETE FROM mysql.user WHERE User='';"
        echo "Result : $?"
        do_query "FLUSH PRIVILEGES;"
        echo "Result : $?"
    fi
}

#Modify my.cnf with settings
#PORT_NUMBER, SKIP_NETWORKING, REMOVE_ANONYMOUS should be passed via env variables
if [ -n "$PORT_NUMBER" ] ; then
    sed  -e "s/3306/$PORT_NUMBER/g" ./my.cnf > ./my.cnf.tmp && mv ./my.cnf.tmp ./my.cnf
fi

if [ -n "$SKIP_NETWORKING" ] ; then
    sed -e "s/#skip-networking/skip-networking/g" ./my.cnf > ./my.cnf.tmp && mv ./my.cnf.tmp ./my.cnf
fi

#Enable using InnoDB
sed -e "s/#innodb_/innodb_/g" ./my.cnf > ./my.cnf.tmp && mv ./my.cnf.tmp ./my.cnf

#Update mysql directory
DEFAULT_MYSQL_DIR=/usr/local/mysql
sed -e "s/`escape $DEFAULT_MYSQL_DIR`/`escape $INSTALLDIR`/g" ./my.cnf > ./my.cnf.tmp && mv ./my.cnf.tmp ./my.cnf


if [ 1 -eq $ISROOT ] ; then
    groupadd mysql
    echo "Result : $?"
    useradd -g mysql mysql 
    echo "Result : $?"
    chown -R mysql "$INSTALLDIR"
    echo "Result : $?"
    chgrp -R mysql "$INSTALLDIR"
    echo "Result : $?"
    chmod -R g+w INSTALLDIR/data
    echo "Result : $?"
fi

if [ 1 -eq $ISROOT ] ; then
    ./scripts/mysql_install_db --user=mysql --no-defaults --defaults-file="$INSTALLDIR"/my.cnf
    echo "Result : $?"
else 
    ./scripts/mysql_install_db --no-defaults --defaults-file="$INSTALLDIR"/my.cnf
    echo "Result : $?"
fi

if [ 1 -eq $ISROOT ] ; then
    chown -R root  "$INSTALLDIR"
    echo "Result : $?"
    chown -R mysql "$INSTALLDIR"/data
    echo "Result : $?"
fi

if [ 1 -eq $ISROOT ] ; then
    ./bin/mysqld_safe --user=mysql --no-defaults &
else 
    ./bin/mysqld_safe --no-defaults &
fi

sleep 3

if [ -n "$PASSWORD" ] ; then
    ./bin/mysqladmin -u root password "$PASSWORD"
    echo "Result : $?"
    ./bin/mysqladmin -u root -h `hostname` password "$PASSWORD"
    echo "Result : $?"
fi

remove_anonymous
remove_remote_root
