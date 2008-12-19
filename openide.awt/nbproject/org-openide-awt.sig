#Signature file v4.0
#Version 7.3.1

CLSS public java.awt.Canvas
cons public Canvas()
cons public Canvas(java.awt.GraphicsConfiguration)
innr protected AccessibleAWTCanvas
intf javax.accessibility.Accessible
meth public java.awt.image.BufferStrategy getBufferStrategy()
meth public javax.accessibility.AccessibleContext getAccessibleContext()
meth public void addNotify()
meth public void createBufferStrategy(int)
meth public void createBufferStrategy(int,java.awt.BufferCapabilities) throws java.awt.AWTException
meth public void paint(java.awt.Graphics)
meth public void update(java.awt.Graphics)
supr java.awt.Component
hfds base,nameCounter,serialVersionUID

CLSS public abstract java.awt.Component
cons protected Component()
fld public final static float BOTTOM_ALIGNMENT = 1.0
fld public final static float CENTER_ALIGNMENT = 0.5
fld public final static float LEFT_ALIGNMENT = 0.0
fld public final static float RIGHT_ALIGNMENT = 1.0
fld public final static float TOP_ALIGNMENT = 0.0
innr protected BltBufferStrategy
innr protected FlipBufferStrategy
innr protected abstract AccessibleAWTComponent
innr public final static !enum BaselineResizeBehavior
intf java.awt.MenuContainer
intf java.awt.image.ImageObserver
intf java.io.Serializable
meth protected boolean requestFocus(boolean)
meth protected boolean requestFocusInWindow(boolean)
meth protected final void disableEvents(long)
meth protected final void enableEvents(long)
meth protected java.awt.AWTEvent coalesceEvents(java.awt.AWTEvent,java.awt.AWTEvent)
meth protected java.lang.String paramString()
meth protected void firePropertyChange(java.lang.String,boolean,boolean)
meth protected void firePropertyChange(java.lang.String,int,int)
meth protected void firePropertyChange(java.lang.String,java.lang.Object,java.lang.Object)
meth protected void processComponentEvent(java.awt.event.ComponentEvent)
meth protected void processEvent(java.awt.AWTEvent)
meth protected void processFocusEvent(java.awt.event.FocusEvent)
meth protected void processHierarchyBoundsEvent(java.awt.event.HierarchyEvent)
meth protected void processHierarchyEvent(java.awt.event.HierarchyEvent)
meth protected void processInputMethodEvent(java.awt.event.InputMethodEvent)
meth protected void processKeyEvent(java.awt.event.KeyEvent)
meth protected void processMouseEvent(java.awt.event.MouseEvent)
meth protected void processMouseMotionEvent(java.awt.event.MouseEvent)
meth protected void processMouseWheelEvent(java.awt.event.MouseWheelEvent)
meth public <%0 extends java.util.EventListener> {%%0}[] getListeners(java.lang.Class<{%%0}>)
meth public boolean action(java.awt.Event,java.lang.Object)
 anno 0 java.lang.Deprecated()
meth public boolean areFocusTraversalKeysSet(int)
meth public boolean contains(int,int)
meth public boolean contains(java.awt.Point)
meth public boolean getFocusTraversalKeysEnabled()
meth public boolean getIgnoreRepaint()
meth public boolean gotFocus(java.awt.Event,java.lang.Object)
 anno 0 java.lang.Deprecated()
meth public boolean handleEvent(java.awt.Event)
 anno 0 java.lang.Deprecated()
meth public boolean hasFocus()
meth public boolean imageUpdate(java.awt.Image,int,int,int,int,int)
meth public boolean inside(int,int)
 anno 0 java.lang.Deprecated()
meth public boolean isBackgroundSet()
meth public boolean isCursorSet()
meth public boolean isDisplayable()
meth public boolean isDoubleBuffered()
meth public boolean isEnabled()
meth public boolean isFocusCycleRoot(java.awt.Container)
meth public boolean isFocusOwner()
meth public boolean isFocusTraversable()
 anno 0 java.lang.Deprecated()
meth public boolean isFocusable()
meth public boolean isFontSet()
meth public boolean isForegroundSet()
meth public boolean isLightweight()
meth public boolean isMaximumSizeSet()
meth public boolean isMinimumSizeSet()
meth public boolean isOpaque()
meth public boolean isPreferredSizeSet()
meth public boolean isShowing()
meth public boolean isValid()
meth public boolean isVisible()
meth public boolean keyDown(java.awt.Event,int)
 anno 0 java.lang.Deprecated()
meth public boolean keyUp(java.awt.Event,int)
 anno 0 java.lang.Deprecated()
meth public boolean lostFocus(java.awt.Event,java.lang.Object)
 anno 0 java.lang.Deprecated()
meth public boolean mouseDown(java.awt.Event,int,int)
 anno 0 java.lang.Deprecated()
meth public boolean mouseDrag(java.awt.Event,int,int)
 anno 0 java.lang.Deprecated()
meth public boolean mouseEnter(java.awt.Event,int,int)
 anno 0 java.lang.Deprecated()
meth public boolean mouseExit(java.awt.Event,int,int)
 anno 0 java.lang.Deprecated()
meth public boolean mouseMove(java.awt.Event,int,int)
 anno 0 java.lang.Deprecated()
meth public boolean mouseUp(java.awt.Event,int,int)
 anno 0 java.lang.Deprecated()
meth public boolean postEvent(java.awt.Event)
 anno 0 java.lang.Deprecated()
meth public boolean prepareImage(java.awt.Image,int,int,java.awt.image.ImageObserver)
meth public boolean prepareImage(java.awt.Image,java.awt.image.ImageObserver)
meth public boolean requestFocusInWindow()
meth public final java.lang.Object getTreeLock()
meth public final void dispatchEvent(java.awt.AWTEvent)
meth public float getAlignmentX()
meth public float getAlignmentY()
meth public int checkImage(java.awt.Image,int,int,java.awt.image.ImageObserver)
meth public int checkImage(java.awt.Image,java.awt.image.ImageObserver)
meth public int getBaseline(int,int)
meth public int getHeight()
meth public int getWidth()
meth public int getX()
meth public int getY()
meth public java.awt.Color getBackground()
meth public java.awt.Color getForeground()
meth public java.awt.Component getComponentAt(int,int)
meth public java.awt.Component getComponentAt(java.awt.Point)
meth public java.awt.Component locate(int,int)
 anno 0 java.lang.Deprecated()
meth public java.awt.Component$BaselineResizeBehavior getBaselineResizeBehavior()
meth public java.awt.ComponentOrientation getComponentOrientation()
meth public java.awt.Container getFocusCycleRootAncestor()
meth public java.awt.Container getParent()
meth public java.awt.Cursor getCursor()
meth public java.awt.Dimension getMaximumSize()
meth public java.awt.Dimension getMinimumSize()
meth public java.awt.Dimension getPreferredSize()
meth public java.awt.Dimension getSize()
meth public java.awt.Dimension getSize(java.awt.Dimension)
meth public java.awt.Dimension minimumSize()
 anno 0 java.lang.Deprecated()
meth public java.awt.Dimension preferredSize()
 anno 0 java.lang.Deprecated()
meth public java.awt.Dimension size()
 anno 0 java.lang.Deprecated()
meth public java.awt.Font getFont()
meth public java.awt.FontMetrics getFontMetrics(java.awt.Font)
meth public java.awt.Graphics getGraphics()
meth public java.awt.GraphicsConfiguration getGraphicsConfiguration()
meth public java.awt.Image createImage(int,int)
meth public java.awt.Image createImage(java.awt.image.ImageProducer)
meth public java.awt.Point getLocation()
meth public java.awt.Point getLocation(java.awt.Point)
meth public java.awt.Point getLocationOnScreen()
meth public java.awt.Point getMousePosition()
meth public java.awt.Point location()
 anno 0 java.lang.Deprecated()
meth public java.awt.Rectangle bounds()
 anno 0 java.lang.Deprecated()
meth public java.awt.Rectangle getBounds()
meth public java.awt.Rectangle getBounds(java.awt.Rectangle)
meth public java.awt.Toolkit getToolkit()
meth public java.awt.dnd.DropTarget getDropTarget()
meth public java.awt.event.ComponentListener[] getComponentListeners()
meth public java.awt.event.FocusListener[] getFocusListeners()
meth public java.awt.event.HierarchyBoundsListener[] getHierarchyBoundsListeners()
meth public java.awt.event.HierarchyListener[] getHierarchyListeners()
meth public java.awt.event.InputMethodListener[] getInputMethodListeners()
meth public java.awt.event.KeyListener[] getKeyListeners()
meth public java.awt.event.MouseListener[] getMouseListeners()
meth public java.awt.event.MouseMotionListener[] getMouseMotionListeners()
meth public java.awt.event.MouseWheelListener[] getMouseWheelListeners()
meth public java.awt.im.InputContext getInputContext()
meth public java.awt.im.InputMethodRequests getInputMethodRequests()
meth public java.awt.image.ColorModel getColorModel()
meth public java.awt.image.VolatileImage createVolatileImage(int,int)
meth public java.awt.image.VolatileImage createVolatileImage(int,int,java.awt.ImageCapabilities) throws java.awt.AWTException
meth public java.awt.peer.ComponentPeer getPeer()
 anno 0 java.lang.Deprecated()
meth public java.beans.PropertyChangeListener[] getPropertyChangeListeners()
meth public java.beans.PropertyChangeListener[] getPropertyChangeListeners(java.lang.String)
meth public java.lang.String getName()
meth public java.lang.String toString()
meth public java.util.Locale getLocale()
meth public java.util.Set<java.awt.AWTKeyStroke> getFocusTraversalKeys(int)
meth public javax.accessibility.AccessibleContext getAccessibleContext()
meth public void add(java.awt.PopupMenu)
meth public void addComponentListener(java.awt.event.ComponentListener)
meth public void addFocusListener(java.awt.event.FocusListener)
meth public void addHierarchyBoundsListener(java.awt.event.HierarchyBoundsListener)
meth public void addHierarchyListener(java.awt.event.HierarchyListener)
meth public void addInputMethodListener(java.awt.event.InputMethodListener)
meth public void addKeyListener(java.awt.event.KeyListener)
meth public void addMouseListener(java.awt.event.MouseListener)
meth public void addMouseMotionListener(java.awt.event.MouseMotionListener)
meth public void addMouseWheelListener(java.awt.event.MouseWheelListener)
meth public void addNotify()
meth public void addPropertyChangeListener(java.beans.PropertyChangeListener)
meth public void addPropertyChangeListener(java.lang.String,java.beans.PropertyChangeListener)
meth public void applyComponentOrientation(java.awt.ComponentOrientation)
meth public void deliverEvent(java.awt.Event)
 anno 0 java.lang.Deprecated()
meth public void disable()
 anno 0 java.lang.Deprecated()
meth public void doLayout()
meth public void enable()
 anno 0 java.lang.Deprecated()
meth public void enable(boolean)
 anno 0 java.lang.Deprecated()
meth public void enableInputMethods(boolean)
meth public void firePropertyChange(java.lang.String,byte,byte)
meth public void firePropertyChange(java.lang.String,char,char)
meth public void firePropertyChange(java.lang.String,double,double)
meth public void firePropertyChange(java.lang.String,float,float)
meth public void firePropertyChange(java.lang.String,long,long)
meth public void firePropertyChange(java.lang.String,short,short)
meth public void hide()
 anno 0 java.lang.Deprecated()
meth public void invalidate()
meth public void layout()
 anno 0 java.lang.Deprecated()
meth public void list()
meth public void list(java.io.PrintStream)
meth public void list(java.io.PrintStream,int)
meth public void list(java.io.PrintWriter)
meth public void list(java.io.PrintWriter,int)
meth public void move(int,int)
 anno 0 java.lang.Deprecated()
meth public void nextFocus()
 anno 0 java.lang.Deprecated()
meth public void paint(java.awt.Graphics)
meth public void paintAll(java.awt.Graphics)
meth public void print(java.awt.Graphics)
meth public void printAll(java.awt.Graphics)
meth public void remove(java.awt.MenuComponent)
meth public void removeComponentListener(java.awt.event.ComponentListener)
meth public void removeFocusListener(java.awt.event.FocusListener)
meth public void removeHierarchyBoundsListener(java.awt.event.HierarchyBoundsListener)
meth public void removeHierarchyListener(java.awt.event.HierarchyListener)
meth public void removeInputMethodListener(java.awt.event.InputMethodListener)
meth public void removeKeyListener(java.awt.event.KeyListener)
meth public void removeMouseListener(java.awt.event.MouseListener)
meth public void removeMouseMotionListener(java.awt.event.MouseMotionListener)
meth public void removeMouseWheelListener(java.awt.event.MouseWheelListener)
meth public void removeNotify()
meth public void removePropertyChangeListener(java.beans.PropertyChangeListener)
meth public void removePropertyChangeListener(java.lang.String,java.beans.PropertyChangeListener)
meth public void repaint()
meth public void repaint(int,int,int,int)
meth public void repaint(long)
meth public void repaint(long,int,int,int,int)
meth public void requestFocus()
meth public void reshape(int,int,int,int)
 anno 0 java.lang.Deprecated()
meth public void resize(int,int)
 anno 0 java.lang.Deprecated()
meth public void resize(java.awt.Dimension)
 anno 0 java.lang.Deprecated()
meth public void setBackground(java.awt.Color)
meth public void setBounds(int,int,int,int)
meth public void setBounds(java.awt.Rectangle)
meth public void setComponentOrientation(java.awt.ComponentOrientation)
meth public void setCursor(java.awt.Cursor)
meth public void setDropTarget(java.awt.dnd.DropTarget)
meth public void setEnabled(boolean)
meth public void setFocusTraversalKeys(int,java.util.Set<? extends java.awt.AWTKeyStroke>)
meth public void setFocusTraversalKeysEnabled(boolean)
meth public void setFocusable(boolean)
meth public void setFont(java.awt.Font)
meth public void setForeground(java.awt.Color)
meth public void setIgnoreRepaint(boolean)
meth public void setLocale(java.util.Locale)
meth public void setLocation(int,int)
meth public void setLocation(java.awt.Point)
meth public void setMaximumSize(java.awt.Dimension)
meth public void setMinimumSize(java.awt.Dimension)
meth public void setName(java.lang.String)
meth public void setPreferredSize(java.awt.Dimension)
meth public void setSize(int,int)
meth public void setSize(java.awt.Dimension)
meth public void setVisible(boolean)
meth public void show()
 anno 0 java.lang.Deprecated()
meth public void show(boolean)
 anno 0 java.lang.Deprecated()
meth public void transferFocus()
meth public void transferFocusBackward()
meth public void transferFocusUpCycle()
meth public void update(java.awt.Graphics)
meth public void validate()
supr java.lang.Object
hfds FOCUS_TRAVERSABLE_DEFAULT,FOCUS_TRAVERSABLE_SET,FOCUS_TRAVERSABLE_UNKNOWN,LOCK,accessibleContext,actionListenerK,adjustmentListenerK,appContext,background,boundsOp,bufferStrategy,changeSupport,coalesceEventsParams,coalesceMap,coalescingEnabled,componentListener,componentListenerK,componentOrientation,componentSerializedDataVersion,containerListenerK,cursor,dbg,dropTarget,enabled,eventCache,eventMask,focusListener,focusListenerK,focusLog,focusTraversalKeyPropertyNames,focusTraversalKeys,focusTraversalKeysEnabled,focusable,font,foreground,graphicsConfig,height,hierarchyBoundsListener,hierarchyBoundsListenerK,hierarchyListener,hierarchyListenerK,ignoreRepaint,incRate,inputMethodListener,inputMethodListenerK,isFocusTraversableOverridden,isInc,isPacked,itemListenerK,keyListener,keyListenerK,locale,log,maxSize,maxSizeSet,minSize,minSizeSet,mouseListener,mouseListenerK,mouseMotionListener,mouseMotionListenerK,mouseWheelListener,mouseWheelListenerK,name,nameExplicitlySet,nativeInLightFixer,newEventsOnly,ownedWindowK,parent,peer,peerFont,popups,prefSize,prefSizeSet,privateKey,requestFocusController,serialVersionUID,textListenerK,valid,visible,width,windowClosingException,windowFocusListenerK,windowListenerK,windowStateListenerK,x,y
hcls AWTTreeLock,BltSubRegionBufferStrategy,DummyRequestFocusController,FlipSubRegionBufferStrategy,NativeInLightFixer,SingleBufferStrategy

CLSS public java.awt.Container
cons public Container()
innr protected AccessibleAWTContainer
meth protected java.lang.String paramString()
meth protected void addImpl(java.awt.Component,java.lang.Object,int)
meth protected void processContainerEvent(java.awt.event.ContainerEvent)
meth protected void processEvent(java.awt.AWTEvent)
meth protected void validateTree()
meth public <%0 extends java.util.EventListener> {%%0}[] getListeners(java.lang.Class<{%%0}>)
meth public boolean areFocusTraversalKeysSet(int)
meth public boolean isAncestorOf(java.awt.Component)
meth public boolean isFocusCycleRoot()
meth public boolean isFocusCycleRoot(java.awt.Container)
meth public boolean isFocusTraversalPolicySet()
meth public final boolean isFocusTraversalPolicyProvider()
meth public final void setFocusTraversalPolicyProvider(boolean)
meth public float getAlignmentX()
meth public float getAlignmentY()
meth public int countComponents()
 anno 0 java.lang.Deprecated()
meth public int getComponentCount()
meth public int getComponentZOrder(java.awt.Component)
meth public java.awt.Component add(java.awt.Component)
meth public java.awt.Component add(java.awt.Component,int)
meth public java.awt.Component add(java.lang.String,java.awt.Component)
meth public java.awt.Component findComponentAt(int,int)
meth public java.awt.Component findComponentAt(java.awt.Point)
meth public java.awt.Component getComponent(int)
meth public java.awt.Component getComponentAt(int,int)
meth public java.awt.Component getComponentAt(java.awt.Point)
meth public java.awt.Component locate(int,int)
 anno 0 java.lang.Deprecated()
meth public java.awt.Component[] getComponents()
meth public java.awt.Dimension getMaximumSize()
meth public java.awt.Dimension getMinimumSize()
meth public java.awt.Dimension getPreferredSize()
meth public java.awt.Dimension minimumSize()
 anno 0 java.lang.Deprecated()
meth public java.awt.Dimension preferredSize()
 anno 0 java.lang.Deprecated()
meth public java.awt.FocusTraversalPolicy getFocusTraversalPolicy()
meth public java.awt.Insets getInsets()
meth public java.awt.Insets insets()
 anno 0 java.lang.Deprecated()
meth public java.awt.LayoutManager getLayout()
meth public java.awt.Point getMousePosition(boolean)
meth public java.awt.event.ContainerListener[] getContainerListeners()
meth public java.util.Set<java.awt.AWTKeyStroke> getFocusTraversalKeys(int)
meth public void add(java.awt.Component,java.lang.Object)
meth public void add(java.awt.Component,java.lang.Object,int)
meth public void addContainerListener(java.awt.event.ContainerListener)
meth public void addNotify()
meth public void addPropertyChangeListener(java.beans.PropertyChangeListener)
meth public void addPropertyChangeListener(java.lang.String,java.beans.PropertyChangeListener)
meth public void applyComponentOrientation(java.awt.ComponentOrientation)
meth public void deliverEvent(java.awt.Event)
 anno 0 java.lang.Deprecated()
meth public void doLayout()
meth public void invalidate()
meth public void layout()
 anno 0 java.lang.Deprecated()
meth public void list(java.io.PrintStream,int)
meth public void list(java.io.PrintWriter,int)
meth public void paint(java.awt.Graphics)
meth public void paintComponents(java.awt.Graphics)
meth public void print(java.awt.Graphics)
meth public void printComponents(java.awt.Graphics)
meth public void remove(int)
meth public void remove(java.awt.Component)
meth public void removeAll()
meth public void removeContainerListener(java.awt.event.ContainerListener)
meth public void removeNotify()
meth public void setComponentZOrder(java.awt.Component,int)
meth public void setFocusCycleRoot(boolean)
meth public void setFocusTraversalKeys(int,java.util.Set<? extends java.awt.AWTKeyStroke>)
meth public void setFocusTraversalPolicy(java.awt.FocusTraversalPolicy)
meth public void setFont(java.awt.Font)
meth public void setLayout(java.awt.LayoutManager)
meth public void transferFocusBackward()
meth public void transferFocusDownCycle()
meth public void update(java.awt.Graphics)
meth public void validate()
supr java.awt.Component
hfds INCLUDE_SELF,SEARCH_HEAVYWEIGHTS,component,containerListener,containerSerializedDataVersion,dbg,descendantsCount,dispatcher,focusCycleRoot,focusTraversalPolicy,focusTraversalPolicyProvider,layoutMgr,listeningBoundsChildren,listeningChildren,modalAppContext,modalComp,ncomponents,printing,printingThreads,serialPersistentFields,serialVersionUID
hcls DropTargetEventTargetFilter,EventTargetFilter,MouseEventTargetFilter,WakingRunnable

CLSS public java.awt.FlowLayout
cons public FlowLayout()
cons public FlowLayout(int)
cons public FlowLayout(int,int,int)
fld public final static int CENTER = 1
fld public final static int LEADING = 3
fld public final static int LEFT = 0
fld public final static int RIGHT = 2
fld public final static int TRAILING = 4
intf java.awt.LayoutManager
intf java.io.Serializable
meth public boolean getAlignOnBaseline()
meth public int getAlignment()
meth public int getHgap()
meth public int getVgap()
meth public java.awt.Dimension minimumLayoutSize(java.awt.Container)
meth public java.awt.Dimension preferredLayoutSize(java.awt.Container)
meth public java.lang.String toString()
meth public void addLayoutComponent(java.lang.String,java.awt.Component)
meth public void layoutContainer(java.awt.Container)
meth public void removeLayoutComponent(java.awt.Component)
meth public void setAlignOnBaseline(boolean)
meth public void setAlignment(int)
meth public void setHgap(int)
meth public void setVgap(int)
supr java.lang.Object
hfds align,alignOnBaseline,currentSerialVersion,hgap,newAlign,serialVersionOnStream,serialVersionUID,vgap

CLSS public abstract interface java.awt.ItemSelectable
meth public abstract java.lang.Object[] getSelectedObjects()
meth public abstract void addItemListener(java.awt.event.ItemListener)
meth public abstract void removeItemListener(java.awt.event.ItemListener)

CLSS public abstract interface java.awt.LayoutManager
meth public abstract java.awt.Dimension minimumLayoutSize(java.awt.Container)
meth public abstract java.awt.Dimension preferredLayoutSize(java.awt.Container)
meth public abstract void addLayoutComponent(java.lang.String,java.awt.Component)
meth public abstract void layoutContainer(java.awt.Container)
meth public abstract void removeLayoutComponent(java.awt.Component)

CLSS public abstract interface java.awt.MenuContainer
meth public abstract boolean postEvent(java.awt.Event)
 anno 0 java.lang.Deprecated()
meth public abstract java.awt.Font getFont()
meth public abstract void remove(java.awt.MenuComponent)

CLSS public abstract java.awt.event.MouseAdapter
cons public MouseAdapter()
intf java.awt.event.MouseListener
intf java.awt.event.MouseMotionListener
intf java.awt.event.MouseWheelListener
meth public void mouseClicked(java.awt.event.MouseEvent)
meth public void mouseDragged(java.awt.event.MouseEvent)
meth public void mouseEntered(java.awt.event.MouseEvent)
meth public void mouseExited(java.awt.event.MouseEvent)
meth public void mouseMoved(java.awt.event.MouseEvent)
meth public void mousePressed(java.awt.event.MouseEvent)
meth public void mouseReleased(java.awt.event.MouseEvent)
meth public void mouseWheelMoved(java.awt.event.MouseWheelEvent)
supr java.lang.Object

CLSS public abstract interface java.awt.event.MouseListener
intf java.util.EventListener
meth public abstract void mouseClicked(java.awt.event.MouseEvent)
meth public abstract void mouseEntered(java.awt.event.MouseEvent)
meth public abstract void mouseExited(java.awt.event.MouseEvent)
meth public abstract void mousePressed(java.awt.event.MouseEvent)
meth public abstract void mouseReleased(java.awt.event.MouseEvent)

CLSS public abstract interface java.awt.event.MouseMotionListener
intf java.util.EventListener
meth public abstract void mouseDragged(java.awt.event.MouseEvent)
meth public abstract void mouseMoved(java.awt.event.MouseEvent)

CLSS public abstract interface java.awt.event.MouseWheelListener
intf java.util.EventListener
meth public abstract void mouseWheelMoved(java.awt.event.MouseWheelEvent)

CLSS public abstract interface java.awt.image.ImageObserver
fld public final static int ABORT = 128
fld public final static int ALLBITS = 32
fld public final static int ERROR = 64
fld public final static int FRAMEBITS = 16
fld public final static int HEIGHT = 2
fld public final static int PROPERTIES = 4
fld public final static int SOMEBITS = 8
fld public final static int WIDTH = 1
meth public abstract boolean imageUpdate(java.awt.Image,int,int,int,int,int)

CLSS public abstract interface java.io.Serializable

CLSS public abstract interface !annotation java.lang.Deprecated
 anno 0 java.lang.annotation.Documented()
 anno 0 java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy value=RUNTIME)
intf java.lang.annotation.Annotation

CLSS public java.lang.Object
cons public Object()
meth protected java.lang.Object clone() throws java.lang.CloneNotSupportedException
meth protected void finalize() throws java.lang.Throwable
meth public boolean equals(java.lang.Object)
meth public final java.lang.Class<?> getClass()
meth public final void notify()
meth public final void notifyAll()
meth public final void wait() throws java.lang.InterruptedException
meth public final void wait(long) throws java.lang.InterruptedException
meth public final void wait(long,int) throws java.lang.InterruptedException
meth public int hashCode()
meth public java.lang.String toString()

CLSS public abstract interface java.lang.Runnable
meth public abstract void run()

CLSS public java.lang.Thread
cons public Thread()
cons public Thread(java.lang.Runnable)
cons public Thread(java.lang.Runnable,java.lang.String)
cons public Thread(java.lang.String)
cons public Thread(java.lang.ThreadGroup,java.lang.Runnable)
cons public Thread(java.lang.ThreadGroup,java.lang.Runnable,java.lang.String)
cons public Thread(java.lang.ThreadGroup,java.lang.Runnable,java.lang.String,long)
cons public Thread(java.lang.ThreadGroup,java.lang.String)
fld public final static int MAX_PRIORITY = 10
fld public final static int MIN_PRIORITY = 1
fld public final static int NORM_PRIORITY = 5
innr public abstract interface static UncaughtExceptionHandler
innr public final static !enum State
intf java.lang.Runnable
meth public boolean isInterrupted()
meth public final boolean isAlive()
meth public final boolean isDaemon()
meth public final int getPriority()
meth public final java.lang.String getName()
meth public final java.lang.ThreadGroup getThreadGroup()
meth public final void checkAccess()
meth public final void join() throws java.lang.InterruptedException
meth public final void join(long) throws java.lang.InterruptedException
meth public final void join(long,int) throws java.lang.InterruptedException
meth public final void resume()
 anno 0 java.lang.Deprecated()
meth public final void setDaemon(boolean)
meth public final void setName(java.lang.String)
meth public final void setPriority(int)
meth public final void stop()
 anno 0 java.lang.Deprecated()
meth public final void stop(java.lang.Throwable)
 anno 0 java.lang.Deprecated()
meth public final void suspend()
 anno 0 java.lang.Deprecated()
meth public int countStackFrames()
 anno 0 java.lang.Deprecated()
meth public java.lang.ClassLoader getContextClassLoader()
meth public java.lang.StackTraceElement[] getStackTrace()
meth public java.lang.String toString()
meth public java.lang.Thread$State getState()
meth public java.lang.Thread$UncaughtExceptionHandler getUncaughtExceptionHandler()
meth public long getId()
meth public static boolean holdsLock(java.lang.Object)
meth public static boolean interrupted()
meth public static int activeCount()
meth public static int enumerate(java.lang.Thread[])
meth public static java.lang.Thread currentThread()
meth public static java.lang.Thread$UncaughtExceptionHandler getDefaultUncaughtExceptionHandler()
meth public static java.util.Map<java.lang.Thread,java.lang.StackTraceElement[]> getAllStackTraces()
meth public static void dumpStack()
meth public static void setDefaultUncaughtExceptionHandler(java.lang.Thread$UncaughtExceptionHandler)
meth public static void sleep(long) throws java.lang.InterruptedException
meth public static void sleep(long,int) throws java.lang.InterruptedException
meth public static void yield()
meth public void destroy()
 anno 0 java.lang.Deprecated()
meth public void interrupt()
meth public void run()
meth public void setContextClassLoader(java.lang.ClassLoader)
meth public void setUncaughtExceptionHandler(java.lang.Thread$UncaughtExceptionHandler)
meth public void start()
supr java.lang.Object
hfds EMPTY_STACK_TRACE,SUBCLASS_IMPLEMENTATION_PERMISSION,blocker,blockerLock,contextClassLoader,daemon,defaultUncaughtExceptionHandler,eetop,group,inheritableThreadLocals,inheritedAccessControlContext,name,nativeParkEventPointer,parkBlocker,priority,single_step,stackSize,stillborn,stopBeforeStart,subclassAudits,target,threadInitNumber,threadLocals,threadQ,threadSeqNumber,threadStatus,throwableFromStop,tid,uncaughtExceptionHandler

CLSS public abstract interface java.lang.annotation.Annotation
meth public abstract boolean equals(java.lang.Object)
meth public abstract int hashCode()
meth public abstract java.lang.Class<? extends java.lang.annotation.Annotation> annotationType()
meth public abstract java.lang.String toString()

CLSS public abstract interface !annotation java.lang.annotation.Documented
 anno 0 java.lang.annotation.Documented()
 anno 0 java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy value=RUNTIME)
 anno 0 java.lang.annotation.Target(java.lang.annotation.ElementType[] value=[ANNOTATION_TYPE])
intf java.lang.annotation.Annotation

CLSS public abstract interface !annotation java.lang.annotation.Retention
 anno 0 java.lang.annotation.Documented()
 anno 0 java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy value=RUNTIME)
 anno 0 java.lang.annotation.Target(java.lang.annotation.ElementType[] value=[ANNOTATION_TYPE])
intf java.lang.annotation.Annotation
meth public abstract java.lang.annotation.RetentionPolicy value()

CLSS public abstract interface !annotation java.lang.annotation.Target
 anno 0 java.lang.annotation.Documented()
 anno 0 java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy value=RUNTIME)
 anno 0 java.lang.annotation.Target(java.lang.annotation.ElementType[] value=[ANNOTATION_TYPE])
intf java.lang.annotation.Annotation
meth public abstract java.lang.annotation.ElementType[] value()

CLSS public abstract interface java.util.EventListener

CLSS public java.util.EventObject
cons public EventObject(java.lang.Object)
fld protected java.lang.Object source
intf java.io.Serializable
meth public java.lang.Object getSource()
meth public java.lang.String toString()
supr java.lang.Object
hfds serialVersionUID

CLSS public abstract interface javax.accessibility.Accessible
meth public abstract javax.accessibility.AccessibleContext getAccessibleContext()

CLSS public abstract javax.swing.AbstractButton
cons public AbstractButton()
fld protected java.awt.event.ActionListener actionListener
fld protected java.awt.event.ItemListener itemListener
fld protected javax.swing.ButtonModel model
fld protected javax.swing.event.ChangeEvent changeEvent
fld protected javax.swing.event.ChangeListener changeListener
fld public final static java.lang.String BORDER_PAINTED_CHANGED_PROPERTY = "borderPainted"
fld public final static java.lang.String CONTENT_AREA_FILLED_CHANGED_PROPERTY = "contentAreaFilled"
fld public final static java.lang.String DISABLED_ICON_CHANGED_PROPERTY = "disabledIcon"
fld public final static java.lang.String DISABLED_SELECTED_ICON_CHANGED_PROPERTY = "disabledSelectedIcon"
fld public final static java.lang.String FOCUS_PAINTED_CHANGED_PROPERTY = "focusPainted"
fld public final static java.lang.String HORIZONTAL_ALIGNMENT_CHANGED_PROPERTY = "horizontalAlignment"
fld public final static java.lang.String HORIZONTAL_TEXT_POSITION_CHANGED_PROPERTY = "horizontalTextPosition"
fld public final static java.lang.String ICON_CHANGED_PROPERTY = "icon"
fld public final static java.lang.String MARGIN_CHANGED_PROPERTY = "margin"
fld public final static java.lang.String MNEMONIC_CHANGED_PROPERTY = "mnemonic"
fld public final static java.lang.String MODEL_CHANGED_PROPERTY = "model"
fld public final static java.lang.String PRESSED_ICON_CHANGED_PROPERTY = "pressedIcon"
fld public final static java.lang.String ROLLOVER_ENABLED_CHANGED_PROPERTY = "rolloverEnabled"
fld public final static java.lang.String ROLLOVER_ICON_CHANGED_PROPERTY = "rolloverIcon"
fld public final static java.lang.String ROLLOVER_SELECTED_ICON_CHANGED_PROPERTY = "rolloverSelectedIcon"
fld public final static java.lang.String SELECTED_ICON_CHANGED_PROPERTY = "selectedIcon"
fld public final static java.lang.String TEXT_CHANGED_PROPERTY = "text"
fld public final static java.lang.String VERTICAL_ALIGNMENT_CHANGED_PROPERTY = "verticalAlignment"
fld public final static java.lang.String VERTICAL_TEXT_POSITION_CHANGED_PROPERTY = "verticalTextPosition"
innr protected ButtonChangeListener
innr protected abstract AccessibleAbstractButton
intf java.awt.ItemSelectable
intf javax.swing.SwingConstants
meth protected int checkHorizontalKey(int,java.lang.String)
meth protected int checkVerticalKey(int,java.lang.String)
meth protected java.awt.event.ActionListener createActionListener()
meth protected java.awt.event.ItemListener createItemListener()
meth protected java.beans.PropertyChangeListener createActionPropertyChangeListener(javax.swing.Action)
meth protected java.lang.String paramString()
meth protected javax.swing.event.ChangeListener createChangeListener()
meth protected void actionPropertyChanged(javax.swing.Action,java.lang.String)
meth protected void addImpl(java.awt.Component,java.lang.Object,int)
meth protected void configurePropertiesFromAction(javax.swing.Action)
meth protected void fireActionPerformed(java.awt.event.ActionEvent)
meth protected void fireItemStateChanged(java.awt.event.ItemEvent)
meth protected void fireStateChanged()
meth protected void init(java.lang.String,javax.swing.Icon)
meth protected void paintBorder(java.awt.Graphics)
meth public boolean getHideActionText()
meth public boolean imageUpdate(java.awt.Image,int,int,int,int,int)
meth public boolean isBorderPainted()
meth public boolean isContentAreaFilled()
meth public boolean isFocusPainted()
meth public boolean isRolloverEnabled()
meth public boolean isSelected()
meth public int getDisplayedMnemonicIndex()
meth public int getHorizontalAlignment()
meth public int getHorizontalTextPosition()
meth public int getIconTextGap()
meth public int getMnemonic()
meth public int getVerticalAlignment()
meth public int getVerticalTextPosition()
meth public java.awt.Insets getMargin()
meth public java.awt.event.ActionListener[] getActionListeners()
meth public java.awt.event.ItemListener[] getItemListeners()
meth public java.lang.Object[] getSelectedObjects()
meth public java.lang.String getActionCommand()
meth public java.lang.String getLabel()
 anno 0 java.lang.Deprecated()
meth public java.lang.String getText()
meth public javax.swing.Action getAction()
meth public javax.swing.ButtonModel getModel()
meth public javax.swing.Icon getDisabledIcon()
meth public javax.swing.Icon getDisabledSelectedIcon()
meth public javax.swing.Icon getIcon()
meth public javax.swing.Icon getPressedIcon()
meth public javax.swing.Icon getRolloverIcon()
meth public javax.swing.Icon getRolloverSelectedIcon()
meth public javax.swing.Icon getSelectedIcon()
meth public javax.swing.event.ChangeListener[] getChangeListeners()
meth public javax.swing.plaf.ButtonUI getUI()
meth public long getMultiClickThreshhold()
meth public void addActionListener(java.awt.event.ActionListener)
meth public void addChangeListener(javax.swing.event.ChangeListener)
meth public void addItemListener(java.awt.event.ItemListener)
meth public void doClick()
meth public void doClick(int)
meth public void removeActionListener(java.awt.event.ActionListener)
meth public void removeChangeListener(javax.swing.event.ChangeListener)
meth public void removeItemListener(java.awt.event.ItemListener)
meth public void removeNotify()
meth public void setAction(javax.swing.Action)
meth public void setActionCommand(java.lang.String)
meth public void setBorderPainted(boolean)
meth public void setContentAreaFilled(boolean)
meth public void setDisabledIcon(javax.swing.Icon)
meth public void setDisabledSelectedIcon(javax.swing.Icon)
meth public void setDisplayedMnemonicIndex(int)
meth public void setEnabled(boolean)
meth public void setFocusPainted(boolean)
meth public void setHideActionText(boolean)
meth public void setHorizontalAlignment(int)
meth public void setHorizontalTextPosition(int)
meth public void setIcon(javax.swing.Icon)
meth public void setIconTextGap(int)
meth public void setLabel(java.lang.String)
 anno 0 java.lang.Deprecated()
meth public void setLayout(java.awt.LayoutManager)
meth public void setMargin(java.awt.Insets)
meth public void setMnemonic(char)
meth public void setMnemonic(int)
meth public void setModel(javax.swing.ButtonModel)
meth public void setMultiClickThreshhold(long)
meth public void setPressedIcon(javax.swing.Icon)
meth public void setRolloverEnabled(boolean)
meth public void setRolloverIcon(javax.swing.Icon)
meth public void setRolloverSelectedIcon(javax.swing.Icon)
meth public void setSelected(boolean)
meth public void setSelectedIcon(javax.swing.Icon)
meth public void setText(java.lang.String)
meth public void setUI(javax.swing.plaf.ButtonUI)
meth public void setVerticalAlignment(int)
meth public void setVerticalTextPosition(int)
meth public void updateUI()
supr javax.swing.JComponent
hfds action,actionPropertyChangeListener,borderPaintedSet,contentAreaFilled,contentAreaFilledSet,defaultCapable,defaultIcon,defaultMargin,disabledIcon,disabledSelectedIcon,handler,hideActionText,horizontalAlignment,horizontalTextPosition,iconTextGap,iconTextGapSet,margin,mnemonic,mnemonicIndex,multiClickThreshhold,paintBorder,paintFocus,pressedIcon,rolloverEnabled,rolloverEnabledSet,rolloverIcon,rolloverSelectedIcon,selectedIcon,setLayout,text,verticalAlignment,verticalTextPosition
hcls ButtonActionPropertyChangeListener,Handler

CLSS public javax.swing.JButton
cons public JButton()
cons public JButton(java.lang.String)
cons public JButton(java.lang.String,javax.swing.Icon)
cons public JButton(javax.swing.Action)
cons public JButton(javax.swing.Icon)
innr protected AccessibleJButton
intf javax.accessibility.Accessible
meth protected java.lang.String paramString()
meth public boolean isDefaultButton()
meth public boolean isDefaultCapable()
meth public java.lang.String getUIClassID()
meth public javax.accessibility.AccessibleContext getAccessibleContext()
meth public void removeNotify()
meth public void setDefaultCapable(boolean)
meth public void updateUI()
supr javax.swing.AbstractButton
hfds uiClassID

CLSS public javax.swing.JCheckBoxMenuItem
cons public JCheckBoxMenuItem()
cons public JCheckBoxMenuItem(java.lang.String)
cons public JCheckBoxMenuItem(java.lang.String,boolean)
cons public JCheckBoxMenuItem(java.lang.String,javax.swing.Icon)
cons public JCheckBoxMenuItem(java.lang.String,javax.swing.Icon,boolean)
cons public JCheckBoxMenuItem(javax.swing.Action)
cons public JCheckBoxMenuItem(javax.swing.Icon)
innr protected AccessibleJCheckBoxMenuItem
intf javax.accessibility.Accessible
intf javax.swing.SwingConstants
meth protected java.lang.String paramString()
meth public boolean getState()
meth public java.lang.Object[] getSelectedObjects()
meth public java.lang.String getUIClassID()
meth public javax.accessibility.AccessibleContext getAccessibleContext()
meth public void setState(boolean)
supr javax.swing.JMenuItem
hfds uiClassID

CLSS public abstract javax.swing.JComponent
cons public JComponent()
fld protected javax.accessibility.AccessibleContext accessibleContext
fld protected javax.swing.event.EventListenerList listenerList
fld protected javax.swing.plaf.ComponentUI ui
fld public final static int UNDEFINED_CONDITION = -1
fld public final static int WHEN_ANCESTOR_OF_FOCUSED_COMPONENT = 1
fld public final static int WHEN_FOCUSED = 0
fld public final static int WHEN_IN_FOCUSED_WINDOW = 2
fld public final static java.lang.String TOOL_TIP_TEXT_KEY = "ToolTipText"
innr public abstract AccessibleJComponent
intf java.io.Serializable
meth protected boolean processKeyBinding(javax.swing.KeyStroke,java.awt.event.KeyEvent,int,boolean)
meth protected boolean requestFocusInWindow(boolean)
meth protected java.awt.Graphics getComponentGraphics(java.awt.Graphics)
meth protected java.lang.String paramString()
meth protected void fireVetoableChange(java.lang.String,java.lang.Object,java.lang.Object) throws java.beans.PropertyVetoException
meth protected void paintBorder(java.awt.Graphics)
meth protected void paintChildren(java.awt.Graphics)
meth protected void paintComponent(java.awt.Graphics)
meth protected void printBorder(java.awt.Graphics)
meth protected void printChildren(java.awt.Graphics)
meth protected void printComponent(java.awt.Graphics)
meth protected void processComponentKeyEvent(java.awt.event.KeyEvent)
meth protected void processKeyEvent(java.awt.event.KeyEvent)
meth protected void processMouseEvent(java.awt.event.MouseEvent)
meth protected void processMouseMotionEvent(java.awt.event.MouseEvent)
meth protected void setUI(javax.swing.plaf.ComponentUI)
meth public <%0 extends java.util.EventListener> {%%0}[] getListeners(java.lang.Class<{%%0}>)
meth public boolean contains(int,int)
meth public boolean getAutoscrolls()
meth public boolean getInheritsPopupMenu()
meth public boolean getVerifyInputWhenFocusTarget()
meth public boolean isDoubleBuffered()
meth public boolean isManagingFocus()
 anno 0 java.lang.Deprecated()
meth public boolean isOpaque()
meth public boolean isOptimizedDrawingEnabled()
meth public boolean isPaintingTile()
meth public boolean isRequestFocusEnabled()
meth public boolean isValidateRoot()
meth public boolean requestDefaultFocus()
 anno 0 java.lang.Deprecated()
meth public boolean requestFocus(boolean)
meth public boolean requestFocusInWindow()
meth public final boolean isPaintingForPrint()
meth public final java.lang.Object getClientProperty(java.lang.Object)
meth public final javax.swing.ActionMap getActionMap()
meth public final javax.swing.InputMap getInputMap()
meth public final javax.swing.InputMap getInputMap(int)
meth public final void putClientProperty(java.lang.Object,java.lang.Object)
meth public final void setActionMap(javax.swing.ActionMap)
meth public final void setInputMap(int,javax.swing.InputMap)
meth public float getAlignmentX()
meth public float getAlignmentY()
meth public int getBaseline(int,int)
meth public int getConditionForKeyStroke(javax.swing.KeyStroke)
meth public int getDebugGraphicsOptions()
meth public int getHeight()
meth public int getWidth()
meth public int getX()
meth public int getY()
meth public java.awt.Component getNextFocusableComponent()
 anno 0 java.lang.Deprecated()
meth public java.awt.Component$BaselineResizeBehavior getBaselineResizeBehavior()
meth public java.awt.Container getTopLevelAncestor()
meth public java.awt.Dimension getMaximumSize()
meth public java.awt.Dimension getMinimumSize()
meth public java.awt.Dimension getPreferredSize()
meth public java.awt.Dimension getSize(java.awt.Dimension)
meth public java.awt.FontMetrics getFontMetrics(java.awt.Font)
meth public java.awt.Graphics getGraphics()
meth public java.awt.Insets getInsets()
meth public java.awt.Insets getInsets(java.awt.Insets)
meth public java.awt.Point getLocation(java.awt.Point)
meth public java.awt.Point getPopupLocation(java.awt.event.MouseEvent)
meth public java.awt.Point getToolTipLocation(java.awt.event.MouseEvent)
meth public java.awt.Rectangle getBounds(java.awt.Rectangle)
meth public java.awt.Rectangle getVisibleRect()
meth public java.awt.event.ActionListener getActionForKeyStroke(javax.swing.KeyStroke)
meth public java.beans.VetoableChangeListener[] getVetoableChangeListeners()
meth public java.lang.String getToolTipText()
meth public java.lang.String getToolTipText(java.awt.event.MouseEvent)
meth public java.lang.String getUIClassID()
meth public javax.accessibility.AccessibleContext getAccessibleContext()
meth public javax.swing.InputVerifier getInputVerifier()
meth public javax.swing.JPopupMenu getComponentPopupMenu()
meth public javax.swing.JRootPane getRootPane()
meth public javax.swing.JToolTip createToolTip()
meth public javax.swing.KeyStroke[] getRegisteredKeyStrokes()
meth public javax.swing.TransferHandler getTransferHandler()
meth public javax.swing.border.Border getBorder()
meth public javax.swing.event.AncestorListener[] getAncestorListeners()
meth public static boolean isLightweightComponent(java.awt.Component)
meth public static java.util.Locale getDefaultLocale()
meth public static void setDefaultLocale(java.util.Locale)
meth public void addAncestorListener(javax.swing.event.AncestorListener)
meth public void addNotify()
meth public void addVetoableChangeListener(java.beans.VetoableChangeListener)
meth public void computeVisibleRect(java.awt.Rectangle)
meth public void disable()
 anno 0 java.lang.Deprecated()
meth public void enable()
 anno 0 java.lang.Deprecated()
meth public void firePropertyChange(java.lang.String,boolean,boolean)
meth public void firePropertyChange(java.lang.String,char,char)
meth public void firePropertyChange(java.lang.String,int,int)
meth public void grabFocus()
meth public void paint(java.awt.Graphics)
meth public void paintImmediately(int,int,int,int)
meth public void paintImmediately(java.awt.Rectangle)
meth public void print(java.awt.Graphics)
meth public void printAll(java.awt.Graphics)
meth public void registerKeyboardAction(java.awt.event.ActionListener,java.lang.String,javax.swing.KeyStroke,int)
meth public void registerKeyboardAction(java.awt.event.ActionListener,javax.swing.KeyStroke,int)
meth public void removeAncestorListener(javax.swing.event.AncestorListener)
meth public void removeNotify()
meth public void removeVetoableChangeListener(java.beans.VetoableChangeListener)
meth public void repaint(java.awt.Rectangle)
meth public void repaint(long,int,int,int,int)
meth public void requestFocus()
meth public void resetKeyboardActions()
meth public void reshape(int,int,int,int)
 anno 0 java.lang.Deprecated()
meth public void revalidate()
meth public void scrollRectToVisible(java.awt.Rectangle)
meth public void setAlignmentX(float)
meth public void setAlignmentY(float)
meth public void setAutoscrolls(boolean)
meth public void setBackground(java.awt.Color)
meth public void setBorder(javax.swing.border.Border)
meth public void setComponentPopupMenu(javax.swing.JPopupMenu)
meth public void setDebugGraphicsOptions(int)
meth public void setDoubleBuffered(boolean)
meth public void setEnabled(boolean)
meth public void setFocusTraversalKeys(int,java.util.Set<? extends java.awt.AWTKeyStroke>)
meth public void setFont(java.awt.Font)
meth public void setForeground(java.awt.Color)
meth public void setInheritsPopupMenu(boolean)
meth public void setInputVerifier(javax.swing.InputVerifier)
meth public void setMaximumSize(java.awt.Dimension)
meth public void setMinimumSize(java.awt.Dimension)
meth public void setNextFocusableComponent(java.awt.Component)
 anno 0 java.lang.Deprecated()
meth public void setOpaque(boolean)
meth public void setPreferredSize(java.awt.Dimension)
meth public void setRequestFocusEnabled(boolean)
meth public void setToolTipText(java.lang.String)
meth public void setTransferHandler(javax.swing.TransferHandler)
meth public void setVerifyInputWhenFocusTarget(boolean)
meth public void setVisible(boolean)
meth public void unregisterKeyboardAction(javax.swing.KeyStroke)
meth public void update(java.awt.Graphics)
meth public void updateUI()
supr java.awt.Container
hfds ACTIONMAP_CREATED,ANCESTOR_INPUTMAP_CREATED,ANCESTOR_NOTIFIER_KEY,ANCESTOR_USING_BUFFER,AUTOSCROLLS_SET,COMPLETELY_OBSCURED,CREATED_DOUBLE_BUFFER,DEBUG_GRAPHICS_LOADED,FOCUS_INPUTMAP_CREATED,FOCUS_TRAVERSAL_KEYS_BACKWARD_SET,FOCUS_TRAVERSAL_KEYS_FORWARD_SET,INHERITS_POPUP_MENU,INPUT_VERIFIER_KEY,INPUT_VERIFIER_SOURCE_KEY,IS_DOUBLE_BUFFERED,IS_OPAQUE,IS_PAINTING_TILE,IS_PRINTING,IS_PRINTING_ALL,IS_REPAINTING,KEYBOARD_BINDINGS_KEY,KEY_EVENTS_ENABLED,NEXT_FOCUS,NOT_OBSCURED,OPAQUE_SET,PARTIALLY_OBSCURED,REQUEST_FOCUS_DISABLED,RESERVED_1,RESERVED_2,RESERVED_3,RESERVED_4,RESERVED_5,RESERVED_6,TRANSFER_HANDLER_KEY,WHEN_IN_FOCUSED_WINDOW_BINDINGS,WIF_INPUTMAP_CREATED,WRITE_OBJ_COUNTER_FIRST,WRITE_OBJ_COUNTER_LAST,aaTextInfo,actionMap,alignmentX,alignmentY,ancestorInputMap,autoscrolls,border,clientProperties,componentObtainingGraphicsFrom,componentObtainingGraphicsFromLock,defaultLocale,flags,focusController,focusInputMap,inputVerifier,isAlignmentXSet,isAlignmentYSet,managingFocusBackwardTraversalKeys,managingFocusForwardTraversalKeys,paintingChild,popupMenu,readObjectCallbacks,tempRectangles,uiClassID,verifyInputWhenFocusTarget,vetoableChangeSupport,windowInputMap
hcls ActionStandin,IntVector,KeyboardState,ReadObjectCallback

CLSS public javax.swing.JList
cons public JList()
cons public JList(java.lang.Object[])
cons public JList(java.util.Vector<?>)
cons public JList(javax.swing.ListModel)
fld public final static int HORIZONTAL_WRAP = 2
fld public final static int VERTICAL = 0
fld public final static int VERTICAL_WRAP = 1
innr protected AccessibleJList
innr public final static DropLocation
intf javax.accessibility.Accessible
intf javax.swing.Scrollable
meth protected java.lang.String paramString()
meth protected javax.swing.ListSelectionModel createSelectionModel()
meth protected void fireSelectionValueChanged(int,int,boolean)
meth public boolean getDragEnabled()
meth public boolean getScrollableTracksViewportHeight()
meth public boolean getScrollableTracksViewportWidth()
meth public boolean getValueIsAdjusting()
meth public boolean isSelectedIndex(int)
meth public boolean isSelectionEmpty()
meth public final javax.swing.DropMode getDropMode()
meth public final javax.swing.JList$DropLocation getDropLocation()
meth public final void setDropMode(javax.swing.DropMode)
meth public int getAnchorSelectionIndex()
meth public int getFirstVisibleIndex()
meth public int getFixedCellHeight()
meth public int getFixedCellWidth()
meth public int getLastVisibleIndex()
meth public int getLayoutOrientation()
meth public int getLeadSelectionIndex()
meth public int getMaxSelectionIndex()
meth public int getMinSelectionIndex()
meth public int getNextMatch(java.lang.String,int,javax.swing.text.Position$Bias)
meth public int getScrollableBlockIncrement(java.awt.Rectangle,int,int)
meth public int getScrollableUnitIncrement(java.awt.Rectangle,int,int)
meth public int getSelectedIndex()
meth public int getSelectionMode()
meth public int getVisibleRowCount()
meth public int locationToIndex(java.awt.Point)
meth public int[] getSelectedIndices()
meth public java.awt.Color getSelectionBackground()
meth public java.awt.Color getSelectionForeground()
meth public java.awt.Dimension getPreferredScrollableViewportSize()
meth public java.awt.Point indexToLocation(int)
meth public java.awt.Rectangle getCellBounds(int,int)
meth public java.lang.Object getPrototypeCellValue()
meth public java.lang.Object getSelectedValue()
meth public java.lang.Object[] getSelectedValues()
meth public java.lang.String getToolTipText(java.awt.event.MouseEvent)
meth public java.lang.String getUIClassID()
meth public javax.accessibility.AccessibleContext getAccessibleContext()
meth public javax.swing.ListCellRenderer getCellRenderer()
meth public javax.swing.ListModel getModel()
meth public javax.swing.ListSelectionModel getSelectionModel()
meth public javax.swing.event.ListSelectionListener[] getListSelectionListeners()
meth public javax.swing.plaf.ListUI getUI()
meth public void addListSelectionListener(javax.swing.event.ListSelectionListener)
meth public void addSelectionInterval(int,int)
meth public void clearSelection()
meth public void ensureIndexIsVisible(int)
meth public void removeListSelectionListener(javax.swing.event.ListSelectionListener)
meth public void removeSelectionInterval(int,int)
meth public void setCellRenderer(javax.swing.ListCellRenderer)
meth public void setDragEnabled(boolean)
meth public void setFixedCellHeight(int)
meth public void setFixedCellWidth(int)
meth public void setLayoutOrientation(int)
meth public void setListData(java.lang.Object[])
meth public void setListData(java.util.Vector<?>)
meth public void setModel(javax.swing.ListModel)
meth public void setPrototypeCellValue(java.lang.Object)
meth public void setSelectedIndex(int)
meth public void setSelectedIndices(int[])
meth public void setSelectedValue(java.lang.Object,boolean)
meth public void setSelectionBackground(java.awt.Color)
meth public void setSelectionForeground(java.awt.Color)
meth public void setSelectionInterval(int,int)
meth public void setSelectionMode(int)
meth public void setSelectionModel(javax.swing.ListSelectionModel)
meth public void setUI(javax.swing.plaf.ListUI)
meth public void setValueIsAdjusting(boolean)
meth public void setVisibleRowCount(int)
meth public void updateUI()
supr javax.swing.JComponent
hfds cellRenderer,dataModel,dragEnabled,dropLocation,dropMode,fixedCellHeight,fixedCellWidth,horizontalScrollIncrement,layoutOrientation,prototypeCellValue,selectionBackground,selectionForeground,selectionListener,selectionModel,uiClassID,visibleRowCount
hcls ListSelectionHandler

CLSS public javax.swing.JMenu
cons public JMenu()
cons public JMenu(java.lang.String)
cons public JMenu(java.lang.String,boolean)
cons public JMenu(javax.swing.Action)
fld protected javax.swing.JMenu$WinListener popupListener
innr protected AccessibleJMenu
innr protected WinListener
intf javax.accessibility.Accessible
intf javax.swing.MenuElement
meth protected java.awt.Point getPopupMenuOrigin()
meth protected java.beans.PropertyChangeListener createActionChangeListener(javax.swing.JMenuItem)
meth protected java.lang.String paramString()
meth protected javax.swing.JMenu$WinListener createWinListener(javax.swing.JPopupMenu)
meth protected javax.swing.JMenuItem createActionComponent(javax.swing.Action)
meth protected void fireMenuCanceled()
meth protected void fireMenuDeselected()
meth protected void fireMenuSelected()
meth protected void processKeyEvent(java.awt.event.KeyEvent)
meth public boolean isMenuComponent(java.awt.Component)
meth public boolean isPopupMenuVisible()
meth public boolean isSelected()
meth public boolean isTearOff()
meth public boolean isTopLevelMenu()
meth public int getDelay()
meth public int getItemCount()
meth public int getMenuComponentCount()
meth public java.awt.Component add(java.awt.Component)
meth public java.awt.Component add(java.awt.Component,int)
meth public java.awt.Component getComponent()
meth public java.awt.Component getMenuComponent(int)
meth public java.awt.Component[] getMenuComponents()
meth public java.lang.String getUIClassID()
meth public javax.accessibility.AccessibleContext getAccessibleContext()
meth public javax.swing.JMenuItem add(java.lang.String)
meth public javax.swing.JMenuItem add(javax.swing.Action)
meth public javax.swing.JMenuItem add(javax.swing.JMenuItem)
meth public javax.swing.JMenuItem getItem(int)
meth public javax.swing.JMenuItem insert(javax.swing.Action,int)
meth public javax.swing.JMenuItem insert(javax.swing.JMenuItem,int)
meth public javax.swing.JPopupMenu getPopupMenu()
meth public javax.swing.MenuElement[] getSubElements()
meth public javax.swing.event.MenuListener[] getMenuListeners()
meth public void addMenuListener(javax.swing.event.MenuListener)
meth public void addSeparator()
meth public void applyComponentOrientation(java.awt.ComponentOrientation)
meth public void doClick(int)
meth public void insert(java.lang.String,int)
meth public void insertSeparator(int)
meth public void menuSelectionChanged(boolean)
meth public void remove(int)
meth public void remove(java.awt.Component)
meth public void remove(javax.swing.JMenuItem)
meth public void removeAll()
meth public void removeMenuListener(javax.swing.event.MenuListener)
meth public void setAccelerator(javax.swing.KeyStroke)
meth public void setComponentOrientation(java.awt.ComponentOrientation)
meth public void setDelay(int)
meth public void setMenuLocation(int,int)
meth public void setModel(javax.swing.ButtonModel)
meth public void setPopupMenuVisible(boolean)
meth public void setSelected(boolean)
meth public void updateUI()
supr javax.swing.JMenuItem
hfds DEBUG,TRACE,VERBOSE,customMenuLocation,delay,listenerRegistry,menuChangeListener,menuEvent,popupMenu,uiClassID
hcls MenuChangeListener

CLSS public javax.swing.JMenuItem
cons public JMenuItem()
cons public JMenuItem(java.lang.String)
cons public JMenuItem(java.lang.String,int)
cons public JMenuItem(java.lang.String,javax.swing.Icon)
cons public JMenuItem(javax.swing.Action)
cons public JMenuItem(javax.swing.Icon)
innr protected AccessibleJMenuItem
intf javax.accessibility.Accessible
intf javax.swing.MenuElement
meth protected java.lang.String paramString()
meth protected void actionPropertyChanged(javax.swing.Action,java.lang.String)
meth protected void configurePropertiesFromAction(javax.swing.Action)
meth protected void fireMenuDragMouseDragged(javax.swing.event.MenuDragMouseEvent)
meth protected void fireMenuDragMouseEntered(javax.swing.event.MenuDragMouseEvent)
meth protected void fireMenuDragMouseExited(javax.swing.event.MenuDragMouseEvent)
meth protected void fireMenuDragMouseReleased(javax.swing.event.MenuDragMouseEvent)
meth protected void fireMenuKeyPressed(javax.swing.event.MenuKeyEvent)
meth protected void fireMenuKeyReleased(javax.swing.event.MenuKeyEvent)
meth protected void fireMenuKeyTyped(javax.swing.event.MenuKeyEvent)
meth protected void init(java.lang.String,javax.swing.Icon)
meth public boolean isArmed()
meth public java.awt.Component getComponent()
meth public java.lang.String getUIClassID()
meth public javax.accessibility.AccessibleContext getAccessibleContext()
meth public javax.swing.KeyStroke getAccelerator()
meth public javax.swing.MenuElement[] getSubElements()
meth public javax.swing.event.MenuDragMouseListener[] getMenuDragMouseListeners()
meth public javax.swing.event.MenuKeyListener[] getMenuKeyListeners()
meth public void addMenuDragMouseListener(javax.swing.event.MenuDragMouseListener)
meth public void addMenuKeyListener(javax.swing.event.MenuKeyListener)
meth public void menuSelectionChanged(boolean)
meth public void processKeyEvent(java.awt.event.KeyEvent,javax.swing.MenuElement[],javax.swing.MenuSelectionManager)
meth public void processMenuDragMouseEvent(javax.swing.event.MenuDragMouseEvent)
meth public void processMenuKeyEvent(javax.swing.event.MenuKeyEvent)
meth public void processMouseEvent(java.awt.event.MouseEvent,javax.swing.MenuElement[],javax.swing.MenuSelectionManager)
meth public void removeMenuDragMouseListener(javax.swing.event.MenuDragMouseListener)
meth public void removeMenuKeyListener(javax.swing.event.MenuKeyListener)
meth public void setAccelerator(javax.swing.KeyStroke)
meth public void setArmed(boolean)
meth public void setEnabled(boolean)
meth public void setModel(javax.swing.ButtonModel)
meth public void setUI(javax.swing.plaf.MenuItemUI)
meth public void updateUI()
supr javax.swing.AbstractButton
hfds DEBUG,TRACE,VERBOSE,accelerator,isMouseDragged,uiClassID
hcls MenuItemFocusListener

CLSS public javax.swing.JPanel
cons public JPanel()
cons public JPanel(boolean)
cons public JPanel(java.awt.LayoutManager)
cons public JPanel(java.awt.LayoutManager,boolean)
innr protected AccessibleJPanel
intf javax.accessibility.Accessible
meth protected java.lang.String paramString()
meth public java.lang.String getUIClassID()
meth public javax.accessibility.AccessibleContext getAccessibleContext()
meth public javax.swing.plaf.PanelUI getUI()
meth public void setUI(javax.swing.plaf.PanelUI)
meth public void updateUI()
supr javax.swing.JComponent
hfds uiClassID

CLSS public javax.swing.JPopupMenu
cons public JPopupMenu()
cons public JPopupMenu(java.lang.String)
innr protected AccessibleJPopupMenu
innr public static Separator
intf javax.accessibility.Accessible
intf javax.swing.MenuElement
meth protected java.beans.PropertyChangeListener createActionChangeListener(javax.swing.JMenuItem)
meth protected java.lang.String paramString()
meth protected javax.swing.JMenuItem createActionComponent(javax.swing.Action)
meth protected void firePopupMenuCanceled()
meth protected void firePopupMenuWillBecomeInvisible()
meth protected void firePopupMenuWillBecomeVisible()
meth protected void paintBorder(java.awt.Graphics)
meth protected void processFocusEvent(java.awt.event.FocusEvent)
meth protected void processKeyEvent(java.awt.event.KeyEvent)
meth public boolean isBorderPainted()
meth public boolean isLightWeightPopupEnabled()
meth public boolean isPopupTrigger(java.awt.event.MouseEvent)
meth public boolean isVisible()
meth public int getComponentIndex(java.awt.Component)
meth public java.awt.Component getComponent()
meth public java.awt.Component getComponentAtIndex(int)
 anno 0 java.lang.Deprecated()
meth public java.awt.Component getInvoker()
meth public java.awt.Insets getMargin()
meth public java.lang.String getLabel()
meth public java.lang.String getUIClassID()
meth public javax.accessibility.AccessibleContext getAccessibleContext()
meth public javax.swing.JMenuItem add(java.lang.String)
meth public javax.swing.JMenuItem add(javax.swing.Action)
meth public javax.swing.JMenuItem add(javax.swing.JMenuItem)
meth public javax.swing.MenuElement[] getSubElements()
meth public javax.swing.SingleSelectionModel getSelectionModel()
meth public javax.swing.event.MenuKeyListener[] getMenuKeyListeners()
meth public javax.swing.event.PopupMenuListener[] getPopupMenuListeners()
meth public javax.swing.plaf.PopupMenuUI getUI()
meth public static boolean getDefaultLightWeightPopupEnabled()
meth public static void setDefaultLightWeightPopupEnabled(boolean)
meth public void addMenuKeyListener(javax.swing.event.MenuKeyListener)
meth public void addPopupMenuListener(javax.swing.event.PopupMenuListener)
meth public void addSeparator()
meth public void insert(java.awt.Component,int)
meth public void insert(javax.swing.Action,int)
meth public void menuSelectionChanged(boolean)
meth public void pack()
meth public void processKeyEvent(java.awt.event.KeyEvent,javax.swing.MenuElement[],javax.swing.MenuSelectionManager)
meth public void processMouseEvent(java.awt.event.MouseEvent,javax.swing.MenuElement[],javax.swing.MenuSelectionManager)
meth public void remove(int)
meth public void removeMenuKeyListener(javax.swing.event.MenuKeyListener)
meth public void removePopupMenuListener(javax.swing.event.PopupMenuListener)
meth public void setBorderPainted(boolean)
meth public void setInvoker(java.awt.Component)
meth public void setLabel(java.lang.String)
meth public void setLightWeightPopupEnabled(boolean)
meth public void setLocation(int,int)
meth public void setPopupSize(int,int)
meth public void setPopupSize(java.awt.Dimension)
meth public void setSelected(java.awt.Component)
meth public void setSelectionModel(javax.swing.SingleSelectionModel)
meth public void setUI(javax.swing.plaf.PopupMenuUI)
meth public void setVisible(boolean)
meth public void show(java.awt.Component,int,int)
meth public void updateUI()
supr javax.swing.JComponent
hfds DEBUG,TRACE,VERBOSE,classLock,defaultLWPopupEnabledKey,desiredLocationX,desiredLocationY,frame,invoker,label,lightWeightPopup,margin,paintBorder,popup,popupPostionFixDisabled,selectionModel,uiClassID

CLSS public javax.swing.JToggleButton
cons public JToggleButton()
cons public JToggleButton(java.lang.String)
cons public JToggleButton(java.lang.String,boolean)
cons public JToggleButton(java.lang.String,javax.swing.Icon)
cons public JToggleButton(java.lang.String,javax.swing.Icon,boolean)
cons public JToggleButton(javax.swing.Action)
cons public JToggleButton(javax.swing.Icon)
cons public JToggleButton(javax.swing.Icon,boolean)
innr protected AccessibleJToggleButton
innr public static ToggleButtonModel
intf javax.accessibility.Accessible
meth protected java.lang.String paramString()
meth public java.lang.String getUIClassID()
meth public javax.accessibility.AccessibleContext getAccessibleContext()
meth public void updateUI()
supr javax.swing.AbstractButton
hfds uiClassID

CLSS public abstract interface javax.swing.ListCellRenderer
meth public abstract java.awt.Component getListCellRendererComponent(javax.swing.JList,java.lang.Object,int,boolean,boolean)

CLSS public abstract interface javax.swing.MenuElement
meth public abstract java.awt.Component getComponent()
meth public abstract javax.swing.MenuElement[] getSubElements()
meth public abstract void menuSelectionChanged(boolean)
meth public abstract void processKeyEvent(java.awt.event.KeyEvent,javax.swing.MenuElement[],javax.swing.MenuSelectionManager)
meth public abstract void processMouseEvent(java.awt.event.MouseEvent,javax.swing.MenuElement[],javax.swing.MenuSelectionManager)

CLSS public abstract interface javax.swing.Scrollable
meth public abstract boolean getScrollableTracksViewportHeight()
meth public abstract boolean getScrollableTracksViewportWidth()
meth public abstract int getScrollableBlockIncrement(java.awt.Rectangle,int,int)
meth public abstract int getScrollableUnitIncrement(java.awt.Rectangle,int,int)
meth public abstract java.awt.Dimension getPreferredScrollableViewportSize()

CLSS public abstract interface javax.swing.SwingConstants
fld public final static int BOTTOM = 3
fld public final static int CENTER = 0
fld public final static int EAST = 3
fld public final static int HORIZONTAL = 0
fld public final static int LEADING = 10
fld public final static int LEFT = 2
fld public final static int NEXT = 12
fld public final static int NORTH = 1
fld public final static int NORTH_EAST = 2
fld public final static int NORTH_WEST = 8
fld public final static int PREVIOUS = 13
fld public final static int RIGHT = 4
fld public final static int SOUTH = 5
fld public final static int SOUTH_EAST = 4
fld public final static int SOUTH_WEST = 6
fld public final static int TOP = 1
fld public final static int TRAILING = 11
fld public final static int VERTICAL = 1
fld public final static int WEST = 7

CLSS public abstract interface javax.swing.event.UndoableEditListener
intf java.util.EventListener
meth public abstract void undoableEditHappened(javax.swing.event.UndoableEditEvent)

CLSS public abstract interface javax.swing.table.TableCellRenderer
meth public abstract java.awt.Component getTableCellRendererComponent(javax.swing.JTable,java.lang.Object,boolean,boolean,int,int)

CLSS public abstract interface javax.swing.tree.TreeCellRenderer
meth public abstract java.awt.Component getTreeCellRendererComponent(javax.swing.JTree,java.lang.Object,boolean,boolean,boolean,int,boolean)

CLSS public javax.swing.undo.AbstractUndoableEdit
cons public AbstractUndoableEdit()
fld protected final static java.lang.String RedoName = "Redo"
fld protected final static java.lang.String UndoName = "Undo"
intf java.io.Serializable
intf javax.swing.undo.UndoableEdit
meth public boolean addEdit(javax.swing.undo.UndoableEdit)
meth public boolean canRedo()
meth public boolean canUndo()
meth public boolean isSignificant()
meth public boolean replaceEdit(javax.swing.undo.UndoableEdit)
meth public java.lang.String getPresentationName()
meth public java.lang.String getRedoPresentationName()
meth public java.lang.String getUndoPresentationName()
meth public java.lang.String toString()
meth public void die()
meth public void redo()
meth public void undo()
supr java.lang.Object
hfds alive,hasBeenDone

CLSS public javax.swing.undo.CompoundEdit
cons public CompoundEdit()
fld protected java.util.Vector<javax.swing.undo.UndoableEdit> edits
meth protected javax.swing.undo.UndoableEdit lastEdit()
meth public boolean addEdit(javax.swing.undo.UndoableEdit)
meth public boolean canRedo()
meth public boolean canUndo()
meth public boolean isInProgress()
meth public boolean isSignificant()
meth public java.lang.String getPresentationName()
meth public java.lang.String getRedoPresentationName()
meth public java.lang.String getUndoPresentationName()
meth public java.lang.String toString()
meth public void die()
meth public void end()
meth public void redo()
meth public void undo()
supr javax.swing.undo.AbstractUndoableEdit
hfds inProgress

CLSS public javax.swing.undo.UndoManager
cons public UndoManager()
intf javax.swing.event.UndoableEditListener
meth protected javax.swing.undo.UndoableEdit editToBeRedone()
meth protected javax.swing.undo.UndoableEdit editToBeUndone()
meth protected void redoTo(javax.swing.undo.UndoableEdit)
meth protected void trimEdits(int,int)
meth protected void trimForLimit()
meth protected void undoTo(javax.swing.undo.UndoableEdit)
meth public boolean addEdit(javax.swing.undo.UndoableEdit)
meth public boolean canRedo()
meth public boolean canUndo()
meth public boolean canUndoOrRedo()
meth public int getLimit()
meth public java.lang.String getRedoPresentationName()
meth public java.lang.String getUndoOrRedoPresentationName()
meth public java.lang.String getUndoPresentationName()
meth public java.lang.String toString()
meth public void discardAllEdits()
meth public void end()
meth public void redo()
meth public void setLimit(int)
meth public void undo()
meth public void undoOrRedo()
meth public void undoableEditHappened(javax.swing.event.UndoableEditEvent)
supr javax.swing.undo.CompoundEdit
hfds indexOfNextAdd,limit

CLSS public abstract interface javax.swing.undo.UndoableEdit
meth public abstract boolean addEdit(javax.swing.undo.UndoableEdit)
meth public abstract boolean canRedo()
meth public abstract boolean canUndo()
meth public abstract boolean isSignificant()
meth public abstract boolean replaceEdit(javax.swing.undo.UndoableEdit)
meth public abstract java.lang.String getPresentationName()
meth public abstract java.lang.String getRedoPresentationName()
meth public abstract java.lang.String getUndoPresentationName()
meth public abstract void die()
meth public abstract void redo()
meth public abstract void undo()

CLSS public org.openide.awt.Actions
cons public Actions()
innr public abstract interface static ButtonActionConnector
innr public abstract interface static SubMenuModel
innr public static CheckboxMenuItem
innr public static MenuItem
innr public static SubMenu
innr public static ToolbarButton
innr public static ToolbarToggleButton
meth public static java.lang.String cutAmpersand(java.lang.String)
meth public static java.lang.String findKey(org.openide.util.actions.SystemAction)
meth public static javax.swing.Action alwaysEnabled(java.awt.event.ActionListener,java.lang.String,java.lang.String,boolean)
meth public static void connect(javax.swing.AbstractButton,javax.swing.Action)
meth public static void connect(javax.swing.AbstractButton,org.openide.util.actions.BooleanStateAction)
meth public static void connect(javax.swing.AbstractButton,org.openide.util.actions.SystemAction)
meth public static void connect(javax.swing.JCheckBoxMenuItem,org.openide.util.actions.BooleanStateAction,boolean)
meth public static void connect(javax.swing.JMenuItem,javax.swing.Action,boolean)
meth public static void connect(javax.swing.JMenuItem,org.openide.util.actions.SystemAction,boolean)
meth public static void setMenuText(javax.swing.AbstractButton,java.lang.String,boolean)
supr java.lang.Object
hfds DISABLED_BUTTON_FILTER,menuActionCache,menuActionLock
hcls BooleanButtonBridge,Bridge,ButtonBridge,CheckMenuBridge,DisabledButtonFilter,ISubActionListener,LazyDisabledIcon,MenuBridge,SubMenuBridge

CLSS public abstract interface static org.openide.awt.Actions$ButtonActionConnector
meth public abstract boolean connect(javax.swing.AbstractButton,javax.swing.Action)
meth public abstract boolean connect(javax.swing.JMenuItem,javax.swing.Action,boolean)

CLSS public static org.openide.awt.Actions$CheckboxMenuItem
cons public CheckboxMenuItem(org.openide.util.actions.BooleanStateAction,boolean)
supr javax.swing.JCheckBoxMenuItem
hfds serialVersionUID

CLSS public static org.openide.awt.Actions$MenuItem
cons public MenuItem(javax.swing.Action,boolean)
cons public MenuItem(org.openide.util.actions.SystemAction,boolean)
intf org.openide.awt.DynamicMenuContent
meth public javax.swing.JComponent[] getMenuPresenters()
meth public javax.swing.JComponent[] synchMenuPresenters(javax.swing.JComponent[])
supr javax.swing.JMenuItem
hfds bridge,serialVersionUID

CLSS public static org.openide.awt.Actions$SubMenu
cons public SubMenu(javax.swing.Action,org.openide.awt.Actions$SubMenuModel,boolean)
cons public SubMenu(org.openide.util.actions.SystemAction,org.openide.awt.Actions$SubMenuModel)
cons public SubMenu(org.openide.util.actions.SystemAction,org.openide.awt.Actions$SubMenuModel,boolean)
intf org.openide.awt.DynamicMenuContent
meth public javax.swing.JComponent[] getMenuPresenters()
meth public javax.swing.JComponent[] synchMenuPresenters(javax.swing.JComponent[])
supr org.openide.awt.JMenuPlus
hfds bridge,serialVersionUID

CLSS public abstract interface static org.openide.awt.Actions$SubMenuModel
meth public abstract int getCount()
meth public abstract java.lang.String getLabel(int)
meth public abstract org.openide.util.HelpCtx getHelpCtx(int)
meth public abstract void addChangeListener(javax.swing.event.ChangeListener)
meth public abstract void performActionAt(int)
meth public abstract void removeChangeListener(javax.swing.event.ChangeListener)

CLSS public static org.openide.awt.Actions$ToolbarButton
cons public ToolbarButton(javax.swing.Action)
cons public ToolbarButton(org.openide.util.actions.SystemAction)
meth public java.awt.Dimension getMaximumSize()
meth public java.awt.Dimension getMinimumSize()
supr org.openide.awt.ToolbarButton
hfds serialVersionUID

CLSS public static org.openide.awt.Actions$ToolbarToggleButton
cons public ToolbarToggleButton(org.openide.util.actions.BooleanStateAction)
meth public java.awt.Dimension getMaximumSize()
meth public java.awt.Dimension getMinimumSize()
supr org.openide.awt.ToolbarToggleButton
hfds serialVersionUID

CLSS public final org.openide.awt.DropDownButtonFactory
fld public final static java.lang.String PROP_DROP_DOWN_MENU = "dropDownMenu"
meth public static javax.swing.JButton createDropDownButton(javax.swing.Icon,javax.swing.JPopupMenu)
meth public static javax.swing.JToggleButton createDropDownToggleButton(javax.swing.Icon,javax.swing.JPopupMenu)
supr java.lang.Object

CLSS public abstract interface org.openide.awt.DynamicMenuContent
meth public abstract javax.swing.JComponent[] getMenuPresenters()
meth public abstract javax.swing.JComponent[] synchMenuPresenters(javax.swing.JComponent[])

CLSS public org.openide.awt.EqualFlowLayout
cons public EqualFlowLayout()
cons public EqualFlowLayout(int)
cons public EqualFlowLayout(int,int,int)
meth public java.awt.Dimension minimumLayoutSize(java.awt.Container)
meth public java.awt.Dimension preferredLayoutSize(java.awt.Container)
meth public void layoutContainer(java.awt.Container)
supr java.awt.FlowLayout
hfds serialVersionUID

CLSS public org.openide.awt.HtmlBrowser
cons public HtmlBrowser()
cons public HtmlBrowser(boolean,boolean)
cons public HtmlBrowser(org.openide.awt.HtmlBrowser$Factory,boolean,boolean)
fld public final static int DEFAULT_HEIGHT = 600
fld public final static int DEFAULT_WIDTH = 400
innr public abstract interface static Factory
innr public abstract static Impl
innr public abstract static URLDisplayer
meth public boolean isStatusLineVisible()
meth public boolean isToolbarVisible()
meth public boolean requestFocusInWindow()
meth public final java.awt.Component getBrowserComponent()
meth public final java.net.URL getDocumentURL()
meth public final org.openide.awt.HtmlBrowser$Impl getBrowserImpl()
meth public final void setEnableHome(boolean)
meth public final void setEnableLocation(boolean)
meth public java.awt.Dimension getPreferredSize()
meth public javax.accessibility.AccessibleContext getAccessibleContext()
meth public static java.lang.String getHomePage()
meth public static void setFactory(org.openide.awt.HtmlBrowser$Factory)
meth public static void setHomePage(java.lang.String)
meth public void requestFocus()
meth public void setStatusLineVisible(boolean)
meth public void setToolbarVisible(boolean)
meth public void setURL(java.lang.String)
meth public void setURL(java.net.URL)
supr javax.swing.JPanel
hfds bBack,bForward,bHistory,bHome,bReload,bStop,browserComponent,browserFactory,browserImpl,browserListener,cbLabel,cbLocation,everythinkIListenInCheckBoxIsUnimportant,head,homePage,iBack,iForward,iHistory,iHome,iReload,iStop,lStatusLine,rp,serialVersionUID,statusLineVisible,toolbarVisible
hcls AccessibleHtmlBrowser,BrowserListener,TrivialURLDisplayer

CLSS public abstract interface static org.openide.awt.HtmlBrowser$Factory
meth public abstract org.openide.awt.HtmlBrowser$Impl createHtmlBrowserImpl()

CLSS public abstract static org.openide.awt.HtmlBrowser$Impl
cons public Impl()
fld public final static java.lang.String PROP_BACKWARD = "backward"
fld public final static java.lang.String PROP_FORWARD = "forward"
fld public final static java.lang.String PROP_HISTORY = "history"
fld public final static java.lang.String PROP_STATUS_MESSAGE = "statusMessage"
fld public final static java.lang.String PROP_TITLE = "title"
fld public final static java.lang.String PROP_URL = "url"
meth public abstract boolean isBackward()
meth public abstract boolean isForward()
meth public abstract boolean isHistory()
meth public abstract java.awt.Component getComponent()
meth public abstract java.lang.String getStatusMessage()
meth public abstract java.lang.String getTitle()
meth public abstract java.net.URL getURL()
meth public abstract void addPropertyChangeListener(java.beans.PropertyChangeListener)
meth public abstract void backward()
meth public abstract void forward()
meth public abstract void reloadDocument()
meth public abstract void removePropertyChangeListener(java.beans.PropertyChangeListener)
meth public abstract void setURL(java.net.URL)
meth public abstract void showHistory()
meth public abstract void stopLoading()
supr java.lang.Object
hfds serialVersionUID

CLSS public abstract static org.openide.awt.HtmlBrowser$URLDisplayer
cons protected URLDisplayer()
meth public abstract void showURL(java.net.URL)
meth public static org.openide.awt.HtmlBrowser$URLDisplayer getDefault()
supr java.lang.Object

CLSS public final org.openide.awt.HtmlRenderer
fld public final static int STYLE_CLIP = 0
fld public final static int STYLE_TRUNCATE = 1
innr public abstract interface static Renderer
meth public final static javax.swing.JLabel createLabel()
meth public final static org.openide.awt.HtmlRenderer$Renderer createRenderer()
meth public static double renderHTML(java.lang.String,java.awt.Graphics,int,int,int,int,java.awt.Font,java.awt.Color,int,boolean)
meth public static double renderPlainString(java.lang.String,java.awt.Graphics,int,int,int,int,java.awt.Font,java.awt.Color,int,boolean)
meth public static double renderString(java.lang.String,java.awt.Graphics,int,int,int,int,java.awt.Font,java.awt.Color,int,boolean)
supr java.lang.Object
hfds LABEL,LOG,STRICT_HTML,STYLE_WORDWRAP,badStrings,colorStack,entities,entitySubstitutions

CLSS public abstract interface static org.openide.awt.HtmlRenderer$Renderer
intf javax.swing.ListCellRenderer
intf javax.swing.table.TableCellRenderer
intf javax.swing.tree.TreeCellRenderer
meth public abstract void reset()
meth public abstract void setCentered(boolean)
meth public abstract void setHtml(boolean)
meth public abstract void setIcon(javax.swing.Icon)
meth public abstract void setIconTextGap(int)
meth public abstract void setIndent(int)
meth public abstract void setParentFocused(boolean)
meth public abstract void setRenderStyle(int)
meth public abstract void setText(java.lang.String)

CLSS public org.openide.awt.JInlineMenu
cons public JInlineMenu()
intf org.openide.awt.DynamicMenuContent
meth public java.awt.Insets getInsets()
meth public javax.swing.JComponent[] getMenuPresenters()
meth public javax.swing.JComponent[] synchMenuPresenters(javax.swing.JComponent[])
meth public void setMenuItems(javax.swing.JMenuItem[])
supr javax.swing.JMenuItem
hfds BLANK_ICON,addedItems,items,serialVersionUID,upToDate

CLSS public org.openide.awt.JMenuPlus
cons public JMenuPlus()
cons public JMenuPlus(java.lang.String)
supr javax.swing.JMenu
hfds serialVersionUID

CLSS public org.openide.awt.JPopupMenuPlus
cons public JPopupMenuPlus()
supr javax.swing.JPopupMenu

CLSS public org.openide.awt.JPopupMenuUtils
cons public JPopupMenuUtils()
meth public static boolean isPopupContained(javax.swing.JPopupMenu)
meth public static java.awt.Rectangle getScreenRect()
meth public static void dynamicChange(javax.swing.JPopupMenu,boolean)
meth public static void dynamicChangeToSubmenu(javax.swing.JPopupMenu,boolean)
supr java.lang.Object
hfds problem,problemTested,reqProc,task

CLSS public org.openide.awt.ListPane
cons public ListPane()
cons public ListPane(java.lang.Object[])
cons public ListPane(java.util.Vector)
cons public ListPane(javax.swing.ListModel)
meth protected void paintBackground(java.awt.Graphics)
meth protected void paintComponent(java.awt.Graphics)
meth public boolean getScrollableTracksViewportHeight()
meth public boolean getScrollableTracksViewportWidth()
meth public boolean isOpaque()
meth public int getScrollableBlockIncrement(java.awt.Rectangle,int,int)
meth public int getScrollableUnitIncrement(java.awt.Rectangle,int,int)
meth public int getVisibleColumnCount()
meth public int locationToIndex(java.awt.Point)
meth public java.awt.Dimension getMinimumSize()
meth public java.awt.Dimension getPreferredScrollableViewportSize()
meth public java.awt.Dimension getPreferredSize()
meth public java.awt.Point indexToLocation(int)
meth public java.awt.Rectangle getCellBounds(int,int)
meth public void addSelectionInterval(int,int)
meth public void ensureIndexIsVisible(int)
meth public void removeSelectionInterval(int,int)
meth public void setSelectionInterval(int,int)
meth public void setVisibleColumnCount(int)
supr javax.swing.JList
hfds dataL,fixedCellHeight,fixedCellWidth,inputL,propertyL,realColumnCount,realRowCount,selectionL,serialVersionUID,updateLayoutStateNeeded,visibleColumnCount,visibleRowCount
hcls DataListener,InputListener,PropertyListener

CLSS public final org.openide.awt.Mnemonics
meth public static int findMnemonicAmpersand(java.lang.String)
meth public static void setLocalizedText(javax.swing.AbstractButton,java.lang.String)
meth public static void setLocalizedText(javax.swing.JLabel,java.lang.String)
supr java.lang.Object

CLSS public org.openide.awt.MouseUtils
cons public MouseUtils()
innr public abstract static PopupMouseAdapter
meth public static boolean isDoubleClick(java.awt.event.MouseEvent)
meth public static boolean isLeftMouseButton(java.awt.event.MouseEvent)
meth public static boolean isRightMouseButton(java.awt.event.MouseEvent)
supr java.lang.Object
hfds DOUBLE_CLICK_DELTA,tempe,temph,tempm,tempx,tempy

CLSS public abstract static org.openide.awt.MouseUtils$PopupMouseAdapter
cons public PopupMouseAdapter()
cons public PopupMouseAdapter(int)
fld public final static int DEFAULT_THRESHOLD = 5
meth protected abstract void showPopup(java.awt.event.MouseEvent)
meth public void mousePressed(java.awt.event.MouseEvent)
meth public void mouseReleased(java.awt.event.MouseEvent)
supr java.awt.event.MouseAdapter

CLSS public org.openide.awt.SpinButton
cons public SpinButton()
fld protected boolean arrowsOrientation
fld protected boolean boundsIgnored
fld protected boolean orientation
fld protected boolean repeating
fld protected boolean running
fld protected boolean runningDir
fld protected int maximum
fld protected int minimum
fld protected int repeatDelay
fld protected int repeatRate
fld protected int step
fld protected int value
fld protected org.openide.awt.SpinButton$RepeatThread rt
fld public final static boolean DEFAULT_ORIENTATION = false
fld public final static int DEFAULT_MAXIMUM = 100
fld public final static int DEFAULT_MINIMUM = 0
fld public final static int DEFAULT_REPEAT_DELAY = 300
fld public final static int DEFAULT_REPEAT_RATE = 70
fld public final static int DEFAULT_STEP = 1
innr protected final RepeatThread
meth protected void repeatThreadNotify()
meth protected void switchRun(boolean)
meth public boolean getArrowsOrientation()
meth public boolean getOrientation()
meth public boolean isBoundsIgnored()
meth public boolean isRepeating()
meth public int getDelay()
meth public int getMaximum()
meth public int getMinimum()
meth public int getRate()
meth public int getStep()
meth public int getValue()
meth public java.awt.Dimension getMinimumSize()
meth public java.awt.Dimension getPreferredSize()
meth public void addPropertyChangeListener(java.beans.PropertyChangeListener)
meth public void addSpinButtonListener(org.openide.awt.SpinButtonListener)
meth public void notifySpinButtonListenersAboutDownMove()
meth public void notifySpinButtonListenersAboutUpMove()
meth public void paint(java.awt.Graphics)
meth public void removePropertyChangeListener(java.beans.PropertyChangeListener)
meth public void removeSpinButtonListener(org.openide.awt.SpinButtonListener)
meth public void setArrowsOrientation(boolean)
meth public void setBoundsIgnored(boolean)
meth public void setDelay(int)
meth public void setForeground(java.awt.Color)
meth public void setMaximum(int)
meth public void setMinimum(int)
meth public void setOrientation(boolean)
meth public void setRate(int)
meth public void setRepeating(boolean)
meth public void setStep(int)
meth public void setValue(int)
meth public void switchStop()
supr java.awt.Canvas
hfds SPIN_DOWN,SPIN_UP,serialVersionUID,spinButtonListeners,valueSupport

CLSS protected final org.openide.awt.SpinButton$RepeatThread
 anno 0 java.lang.Deprecated()
meth public void run()
supr java.lang.Thread
hfds finish

CLSS public org.openide.awt.SpinButtonAdapter
cons public SpinButtonAdapter()
intf org.openide.awt.SpinButtonListener
meth public void changeValue()
meth public void moveDown()
meth public void moveUp()
supr java.lang.Object

CLSS public abstract interface org.openide.awt.SpinButtonListener
meth public abstract void changeValue()
meth public abstract void moveDown()
meth public abstract void moveUp()

CLSS public org.openide.awt.SplittedPanel
cons public SplittedPanel()
fld public final static int DEFAULT_SPLITTER = 0
fld public final static int EMPTY_SPLITTER = 1
fld public final static int FIRST_PREFERRED = -1
fld public final static int HORIZONTAL = 2
fld public final static int NONE = 0
fld public final static int RAISED_SPLITTER = 0
fld public final static int SECOND_PREFERRED = -2
fld public final static int VERTICAL = 1
fld public final static java.lang.Object ADD_BOTTOM
fld public final static java.lang.Object ADD_FIRST
fld public final static java.lang.Object ADD_LEFT
fld public final static java.lang.Object ADD_RIGHT
fld public final static java.lang.Object ADD_SECOND
fld public final static java.lang.Object ADD_SPLITTER
fld public final static java.lang.Object ADD_TOP
innr public abstract interface static SplitChangeListener
innr public static EmptySplitter
innr public static SplitChangeEvent
intf javax.accessibility.Accessible
meth protected void computeSizesAfterFlip()
meth protected void fireSplitChange(int,int)
meth protected void setSplitterCursor()
meth protected void updatePopupMenu()
meth protected void updateSplitting()
meth public boolean getKeepFirstSame()
meth public boolean getKeepSecondSame()
meth public boolean getPanesSwapped()
meth public boolean isContinuousLayout()
meth public boolean isSplitAbsolute()
meth public boolean isSplitDragable()
meth public boolean isSplitTypeChangeEnabled()
meth public boolean isSwapPanesEnabled()
meth public int getSplitPosition()
meth public int getSplitType()
meth public int getSplitterType()
meth public java.awt.Component getSplitterComponent()
meth public javax.accessibility.AccessibleContext getAccessibleContext()
meth public void addSplitChangeListener(org.openide.awt.SplittedPanel$SplitChangeListener)
meth public void removeSplitChangeListener(org.openide.awt.SplittedPanel$SplitChangeListener)
meth public void setContinuousLayout(boolean)
meth public void setKeepFirstSame(boolean)
meth public void setKeepSecondSame(boolean)
meth public void setSplitAbsolute(boolean)
meth public void setSplitDragable(boolean)
meth public void setSplitPosition(int)
meth public void setSplitType(int)
meth public void setSplitTypeChangeEnabled(boolean)
meth public void setSplitterComponent(java.awt.Component)
meth public void setSplitterType(int)
meth public void setSwapPanesEnabled(boolean)
meth public void swapPanes()
meth public void updateUI()
supr javax.swing.JComponent
hfds DEFAULT_SPLIT_TYPE,absolute,accessibleContext,continuousLayout,descriptionFormat,dragPos,dragable,drawBumps,firstComponent,horizontalCMI,keepFirstSame,keepSecondSame,listeners,mouseAdapter,nameFormat,panesSwapped,popupMenu,popupMenuEnabled,resetPosition,secondComponent,serialVersionUID,splitIsChanging,splitPosition,splitType,splitTypeChangeEnabled,splitter,splitterCMI,splitterType,swapCMI,swapPanesEnabled,verticalCMI
hcls DefaultSplitter,MouseListenerAdapter,SplitLayout

CLSS public static org.openide.awt.SplittedPanel$EmptySplitter
cons public EmptySplitter()
cons public EmptySplitter(int)
intf javax.accessibility.Accessible
meth public java.awt.Dimension getPreferredSize()
meth public javax.accessibility.AccessibleContext getAccessibleContext()
supr javax.swing.JComponent
hfds accessibleContext,serialVersionUID,width

CLSS public static org.openide.awt.SplittedPanel$SplitChangeEvent
cons public SplitChangeEvent(org.openide.awt.SplittedPanel,int,int)
meth public int getNewValue()
meth public int getOldValue()
supr java.util.EventObject
hfds newValue,oldValue,serialVersionUID

CLSS public abstract interface static org.openide.awt.SplittedPanel$SplitChangeListener
meth public abstract void splitChanged(org.openide.awt.SplittedPanel$SplitChangeEvent)

CLSS public abstract org.openide.awt.StatusDisplayer
cons protected StatusDisplayer()
meth public abstract java.lang.String getStatusText()
meth public abstract void addChangeListener(javax.swing.event.ChangeListener)
meth public abstract void removeChangeListener(javax.swing.event.ChangeListener)
meth public abstract void setStatusText(java.lang.String)
meth public static org.openide.awt.StatusDisplayer getDefault()
supr java.lang.Object
hfds INSTANCE
hcls Trivial

CLSS public abstract interface org.openide.awt.StatusLineElementProvider
meth public abstract java.awt.Component getStatusLineElement()

CLSS public org.openide.awt.TabbedPaneFactory
fld public final static java.lang.String PROP_CLOSE = "close"
meth public static javax.swing.JTabbedPane createCloseButtonTabbedPane()
supr java.lang.Object

CLSS public org.openide.awt.ToolbarButton
cons public ToolbarButton()
cons public ToolbarButton(javax.swing.Icon)
meth public void processMouseEvent(java.awt.event.MouseEvent)
supr javax.swing.JButton
hfds serialVersionUID

CLSS public org.openide.awt.ToolbarToggleButton
cons public ToolbarToggleButton()
cons public ToolbarToggleButton(javax.swing.Icon)
cons public ToolbarToggleButton(javax.swing.Icon,boolean)
supr javax.swing.JToggleButton
hfds serialVersionUID

CLSS public abstract interface org.openide.awt.UndoRedo
fld public final static org.openide.awt.UndoRedo NONE
innr public final static Empty
innr public static Manager
meth public abstract boolean canRedo()
meth public abstract boolean canUndo()
meth public abstract java.lang.String getRedoPresentationName()
meth public abstract java.lang.String getUndoPresentationName()
meth public abstract void addChangeListener(javax.swing.event.ChangeListener)
meth public abstract void redo()
meth public abstract void removeChangeListener(javax.swing.event.ChangeListener)
meth public abstract void undo()

CLSS public final static org.openide.awt.UndoRedo$Empty
cons public Empty()
intf org.openide.awt.UndoRedo
meth public boolean canRedo()
meth public boolean canUndo()
meth public java.lang.String getRedoPresentationName()
meth public java.lang.String getUndoPresentationName()
meth public void addChangeListener(javax.swing.event.ChangeListener)
meth public void redo()
meth public void removeChangeListener(javax.swing.event.ChangeListener)
meth public void undo()
supr java.lang.Object

CLSS public static org.openide.awt.UndoRedo$Manager
cons public Manager()
intf org.openide.awt.UndoRedo
meth public boolean canUndo()
meth public java.lang.String getRedoPresentationName()
meth public java.lang.String getUndoPresentationName()
meth public void addChangeListener(javax.swing.event.ChangeListener)
meth public void discardAllEdits()
meth public void removeChangeListener(javax.swing.event.ChangeListener)
meth public void undoableEditHappened(javax.swing.event.UndoableEditEvent)
supr javax.swing.undo.UndoManager
hfds cs,runus,serialVersionUID,task

