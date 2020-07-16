package com.bingis_khan.shitty_roguelike.engine.main;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.bingis_khan.shitty_roguelike.engine.display.Display;

@DisplayName("Given a new Engine instance")
public class EngineTest {
	private boolean onAdd, onRemove;
	private Engine e;
	
	class OnRemove extends State {

		@Override
		public void onAdd(Engine e) {}

		@Override
		public void onRemove(Engine e) {
			onRemove = true;
		}

		@Override
		public void render(Display d) {}

		@Override
		public void update(Engine e) {}
		
	}
	
	class OnAdd extends State {

		@Override
		public void onAdd(Engine e) {
			onAdd = true;
		}

		@Override
		public void onRemove(Engine e) {}

		@Override
		public void render(Display d) {}

		@Override
		public void update(Engine e) {}
		
	}
	
	@Nested
	@DisplayName("calling overlayState")
	class CallingOverlayState {
		
		@BeforeEach
		void setup() {
			e = new Engine(60, 10, 10, 
					new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB), 
					"test");
			onAdd = false;
			onRemove = false;
		}
		
		@Test
		@DisplayName("should invoke state's onAdd.")
		public void shouldInvokeStatesOnAdd() {
			State s = new OnAdd();
			e.overlayState(s);
			assertTrue(onAdd);
		}
		
		@Test
		@DisplayName("should NOT invoke state's onRemove.")
		public void shouldNotInvokeStatesOnRemove() {
			State s = new OnRemove();
			e.overlayState(s);
			assertFalse(onRemove);
		}
		
		@Test
		@DisplayName("should not remove previous state.")
		public void shouldNotRemovePreviousState() {
			State rem = new OnRemove();
			State over = new OnAdd();
			
			e.overlayState(rem);
			e.overlayState(over);
			
			assertFalse(onRemove);
		}
	}
	
	@Nested
	@DisplayName("calling removeActiveState")
	public class CallingRemoveActiveState {
		
		@BeforeEach
		void setup() {
			e = new Engine(60, 10, 10, 
					new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB), 
					"test");
			onAdd = false;
			onRemove = false;
		}
		
		@Test
		@DisplayName("should NOT invoke state's onAdd.")
		public void shouldNotInvokeStatesOnAdd() {
			State s = new OnAdd();
			e.overlayState(s);
			
			onAdd = false; // Clear flags.
			
			e.removeActiveState();
			assertFalse(onAdd);
		}
		
		@Test
		@DisplayName("should invoke state's onRemove.")
		public void shouldInvokeStatesOnRemove() {
			State s = new OnRemove();
			e.overlayState(s);
			e.removeActiveState();
			assertTrue(onRemove);
		}
		
		@Test
		@DisplayName("should throw exception if called twice in a single turn.")
		public void shouldThrowExceptionIfCalledTwiceInASingleTurn() {
			State s1 = new OnRemove();
			State s2 = new OnRemove();
			
			e.overlayState(s1);
			e.overlayState(s2);
			
			e.removeActiveState();
			assertThrows(RuntimeException.class, () -> e.removeActiveState());
		}
	}
	
	@Nested
	@DisplayName("calling switchState")
	public class CallingSwitchState {
		
		@BeforeEach
		void setup() {
			e = new Engine(60, 10, 10, 
					new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB), 
					"test");
			onAdd = false;
			onRemove = false;
		}
		
		@Test
		@DisplayName("should invoke new state's onAdd.")
		public void shouldInvokeNewStatesOnAdd() {
			State oldState = new OnRemove();
			State newState = new OnAdd();
			e.overlayState(oldState);
			e.switchState(newState);
			assertTrue(onAdd);
		}
		
		@Test
		@DisplayName("should invoke old state's onRemove.")
		public void shoulInvokeOldStatesOnRemove() {
			State oldState = new OnRemove();
			State newState = new OnAdd();
			e.overlayState(oldState);
			e.switchState(newState);
			assertTrue(onRemove);
		}
		
		@Test
		@DisplayName("should NOT invoke old state's onAdd.")
		public void shouldNotInvokeOldStatesOnAdd() {
			State oldState = new OnAdd();
			State newState = new OnRemove();
			e.overlayState(oldState);
			
			onAdd = false; // Clear state.
			
			e.switchState(newState);
			assertFalse(onAdd);
		}
		
		@Test
		@DisplayName("should NOT invoke new state's onRemove.")
		public void shouldNotInvokeNewStatesOnRemove() {
			State oldState = new OnAdd();
			State newState = new OnRemove();
			e.overlayState(oldState);
			e.switchState(newState);
			assertFalse(onRemove);
		}
		
	}
}
