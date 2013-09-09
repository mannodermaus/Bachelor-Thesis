package de.hsb.ms.syn.common.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;

import de.hsb.ms.syn.common.util.Utils;

/**
 * Custom UI element displaying a window that may be swiped in and out of the screen
 * (Unused, incomplete implementation)
 * @author Marcel
 *
 */
public class DragWindow extends Window {
	
	/**
	 * Allowed drag directions
	 * @author Marcel
	 */
	public enum DragDirection { HORIZONTAL, VERTICAL, BOTH };
	
	// Local offset of the mouse in relation to lower-left corner of the actor
	private float mPositionOffsetX;
	private float mPositionOffsetY;
	
	// Global mouse/touch position on initial touch event (used to determine if the swipe motion is valid using distance calc.)
	private float mTouchReferencePositionX;
	private float mTouchReferencePositionY;
	
	// Threshold value used at distance calculation time. If distance > this value, it's a valid motion
	private float mThreshold = 50f;
	
	// Drag direction that this DragWindow may be dragged across
	private DragDirection mDirection;
	
	// Indicator that the current swipe is a valid motion
	private boolean  mValidMotion;
	
	// Indicator if the DragWindow is currently "active", i.e. moved into the screen, or moved out of the screen
	private boolean mIsMovedIn;
	
	public DragWindow(Skin skin) {
		super("DragWindow", skin);
		this.init();
	}
	
	private void init() {
		// TODO Make this a property of a DragWindowStyle
		mDirection = DragDirection.VERTICAL;
		
		mIsMovedIn = false;
		mValidMotion = false;
		
		// Init listeners
		// Delete the "old" LibGDX listener first, we don't want that
		this.getListeners().clear();
		
		// Now initialize the new custom DragListener
		// TODO Instead of attaching this to the Window itself, use a "title bar"-like element to hold this listener
		DragListener dlistener = new DragListener() {
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if (mIsMovedIn) {
					// If the component is moved in already, a touch is enough to "move it out" again.
					resetPosition();
					moveComponentOut();
					// In this case, the event will be caught instead of delegated. The handling is over
					return true;
				} else {
					// Save local position offset of pointer in relation to window
					mPositionOffsetX = x;
					mPositionOffsetY = y;
					// Save absolute touch coordinates
					mTouchReferencePositionX = event.getStageX();
					mTouchReferencePositionY = event.getStageY();
					return super.touchDown(event, x, y, pointer, button);
				}
			}
			
			@Override
			public void drag(InputEvent event, float x, float y, int pointer) {
				// Get absolute touch coordinates
				float currentMousePosX = event.getStageX();
				float currentMousePosY = event.getStageY();
				// Calculate drag distance
				float distance = Utils.dst2d(currentMousePosX, currentMousePosY, mTouchReferencePositionX, mTouchReferencePositionY);
				
				// Check the distance that the pointer has moved and compare it to the threshold value
				// that enables a valid swipe. Also check if the swipe's direction is OK by comparing
				// the UI element's current position and checking if it has moved at all
				if (distance > mThreshold
						&& (DragWindow.this.getX() != (mTouchReferencePositionX - mPositionOffsetX)
						|| DragWindow.this.getY() != (mTouchReferencePositionY - mPositionOffsetY))) {
					// Swipe will be executed upon releasing the pointer
					mValidMotion = true;
				} else {
					// No swipe will be executed: Reset the element to its original position when the pointer is released
					mValidMotion = false;
					float px = (mDirection == DragDirection.VERTICAL)	? mTouchReferencePositionX - mPositionOffsetX
																		: currentMousePosX - mPositionOffsetX;
					float py = (mDirection == DragDirection.HORIZONTAL) ? mTouchReferencePositionY - mPositionOffsetY
																		: currentMousePosY - mPositionOffsetY;
					DragWindow.this.setPosition(px, py);
				}
				super.drag(event, x, y, pointer);
			}
			
			@Override
			public void dragStop(InputEvent event, float x, float y, int pointer) {
				super.dragStop(event, x, y, pointer);
				if (mValidMotion) {
					// A valid motion has occurred; flip the component's state and "move it in" or "out"
					if (mIsMovedIn) {
						moveComponentOut();
					} else {
						moveComponentIn();
					}
				} else {
					// No valid motion has occurred; reset the component's position to its original state
					resetPosition();
				}
			}
		};
		dlistener.setTapSquareSize(2f);
		this.addListener(dlistener);
	}
	
	private void resetPosition() {
		this.setPosition(mTouchReferencePositionX - mPositionOffsetX,
						mTouchReferencePositionY - mPositionOffsetY);
	}
	
	private void moveComponentOut() {
		// do things here
		// ...
		Utils.log("moving that guy out, YEAH");
		mIsMovedIn = false;
	}
	
	private void moveComponentIn() {
		// do things here
		// ...
		Utils.log("moving that guy in, YEAH");
		mIsMovedIn = true;
	}
	
	/**
	 * Set a new threshold value. This value determines the distance that the user needs to drag the window
	 * in order to engage the swipe-in motion
	 * @param value
	 */
	public void setThreshold(float value) {
		this.mThreshold = value;
	}
	
	/**
	 * Get the current threshold value.
	 * @return
	 */
	public float getThreshold() {
		return this.mThreshold;
	}
}
