set -x

DIRNAME=`dirname $0`
cd ${DIRNAME}
source init.sh

mkdir -p $NB_ALL
cd  $NB_ALL

###################################################################
#
# Checkout all the required NB modules
#
###################################################################

#nbbuild module is required for the list of modules
cvs -d :pserver:anoncvs@cvs.netbeans.org:/cvs checkout -D "$CVS_STAMP" nbbuild > $CVS_CHECKOUT_LOG 2>&1
ERROR_CODE=$?

if [ $ERROR_CODE != 0 ]; then
    echo "ERROR: $ERROR_CODE - Checkout of nbbuild module failed"
    exit $ERROR_CODE;
fi

#Checkout the rest of required modules for the NB IDE itself
ant -f nbbuild/build.xml checkout >> $CVS_CHECKOUT_LOG 2>&1
ERROR_CODE=$?

if [ $ERROR_CODE != 0 ]; then
    echo "ERROR: $ERROR_CODE - Checkout of ide modules failed"
    exit $ERROR_CODE;
fi

#Checkout modules for the components
cvs -z6 -d :pserver:anoncvs@cvs.netbeans.org:/cvs checkout -D "$CVS_STAMP" mobility uml visualweb enterprise print identity  >> $CVS_CHECKOUT_LOG 2>&1
ERROR_CODE=$?

if [ $ERROR_CODE != 0 ]; then
    echo "ERROR: $ERROR_CODE - Checkout of the rest of components failed"
    exit $ERROR_CODE;
fi
