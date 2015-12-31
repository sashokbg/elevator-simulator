package bg.alexander;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bg.alexander.elevator.states.DoorsClosed;
import bg.alexander.elevator.states.DoorsOpened;
import bg.alexander.simulator.SimulatorContext;
import bg.alexander.simulator.Event;
import bg.alexander.simulator.Simulator;

public class SimulatorTest {
	private Simulator simulator;
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

	@Before
	public void setUp() {
		simulator = new Simulator();
		simulator.setTickTime(1);
	}

	@Test
	public void stringVsStringBuilder(){
		String[] strings = {"zero","one","two","three","four","five","six","seven","eight","nine"};
		
		System.out.println("Str concat start");
		long start = System.currentTimeMillis();
		for(int i =0; i < 999999; i++){
			String result = "";
			for(int j =0; j < 9; j++){
				result+=strings[j];
			}
		}
		long end=System.currentTimeMillis();
		System.out.println("-- Result -- "+(end-start));
		
		System.out.println("StringBuilder start");
		start = System.currentTimeMillis();
		for(int i =0; i < 999999; i++){
			StringBuilder resultb = new StringBuilder();
			for(int j =0; j < 9; j++){
				resultb.append(strings[j]);
			}
		}
		end=System.currentTimeMillis();
		System.out.println("-- Result -- "+(end-start));
	}
	
	@Test
	public void testSimpleTransitionOpening() throws InterruptedException {
		SimulatorContext ct = new SimulatorContext(simulator);
		assertEquals(ct.getState().getClass(), DoorsClosed.class);
		simulator.addEvent(new Event((w) -> ct.pressButton(), "Press button"));

		simulator.run();
		assertEquals(ct.getState().getClass(), DoorsOpened.class);
	}
	
	@Test
	public void testSimpleTransitionClosing() throws InterruptedException {
		SimulatorContext ct = new SimulatorContext(simulator);
		ct.setState(new DoorsOpened());
		assertEquals(ct.getState().getClass(), DoorsOpened.class);
		simulator.addEvent(new Event((w) -> ct.pressButton(), "Press button"));

		simulator.run();
		assertEquals(ct.getState().getClass(), DoorsClosed.class);
	}

	@Test
	public void testReverseTransition() throws InterruptedException {
		SimulatorContext ct = new SimulatorContext(simulator);
		assertEquals(ct.getState().getClass(), DoorsClosed.class);
		simulator.addEvent(new Event((w) -> ct.pressButton(), "Press button"));

		Event pressButtonEvent = new Event();
		pressButtonEvent.setTime(5);
		pressButtonEvent.setMessage("Press button - again");
		pressButtonEvent.setAction((w) -> ct.pressButton());
		simulator.addEvent(pressButtonEvent);

		simulator.run();
		assertEquals(ct.getState().getClass(), DoorsClosed.class);
	}

	@Test
	public void testDoubleReverseTransition() throws InterruptedException {
		SimulatorContext ct = new SimulatorContext(simulator);
		assertEquals(ct.getState().getClass(), DoorsClosed.class);
		// pressButtonEvent.setAction();
		simulator.addEvent(new Event((w) -> ct.pressButton(), "Press button"));

		Event pressButtonEvent = new Event();
		pressButtonEvent.setTime(20);
		pressButtonEvent.setMessage("Press button - again");
		pressButtonEvent.setAction((w) -> ct.pressButton());
		simulator.addEvent(pressButtonEvent);

		pressButtonEvent = new Event();
		pressButtonEvent.setTime(25);
		pressButtonEvent.setMessage("Press button - again");
		pressButtonEvent.setAction((w) -> ct.pressButton());
		simulator.addEvent(pressButtonEvent);

		simulator.run();
		assertEquals(ct.getState().getClass(), DoorsOpened.class);
	}

	@Test
	public void testSimulator() throws InterruptedException {
		System.setOut(new PrintStream(outContent));
		System.setErr(new PrintStream(errContent));

		Event innerIvent = new Event(32);
		Event instantEvent = new Event();
		instantEvent.setAction((w) -> System.out.println("Instant event " + w));

		innerIvent.setAction((w) -> System.out.println("Inner event " + w));

		Event e = new Event(28);
		e.setAction((w) -> {
			simulator.addEvent(innerIvent);
			simulator.addEvent(instantEvent);
			System.out.print("New event "+w+" ");
		});
		simulator.addEvent(e);
		simulator.run();
		
//		assertEquals("New event 29 Instant event 30 Inner event 62 ", outContent.toString());
	}
	
	@After
	public void cleanUpStreams() {
	    System.setOut(null);
	    System.setErr(null);
	}
}
