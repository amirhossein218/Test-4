package org.springframework.samples.petclinic.utility;

import com.github.mryf323.tractatus.*;
import com.github.mryf323.tractatus.experimental.extensions.ReportingExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ClauseDefinition(clause = 'a', def = "t1A != t2A")
@ClauseDefinition(clause = 'b', def = "t1B != t2B")
@ClauseDefinition(clause = 'c', def = "t1C != t2C")
@ClauseDefinition(clause = 'd', def = "t1A < 0")
@ClauseDefinition(clause = 'e', def = "t1A + t1B < t1C")
@ExtendWith(ReportingExtension.class)
class TriCongruenceTest {

	private static final Logger log = LoggerFactory.getLogger(TriCongruenceTest.class);


//	  Predicate: a + b + c
//	  Order a, b, c
//
//	  a: UTP : {TFF}, NFP : {FFF}
//
//	  b: UTP : {FTF}, NFP : {FFF}
//
//	  c: UTP : {FFT}, NFP : {FFF}
//
//	  CUTPNFP : {TFF, FTF, FFT, FFF}



	@Test
	public void sampleTest() {
		Triangle t1 = new Triangle(2, 3, 7);
		Triangle t2 = new Triangle(7, 2, 3);
		boolean areCongruent = TriCongruence.areCongruent(t1, t2);
		log.debug("Triangles identified as '{}'.", areCongruent ? "Congruent" : "Not Congruent");
		Assertions.assertFalse(areCongruent);
	}

	public boolean FirstPredicate(double t1A, double t2A, double t1B, double t2B, double t1C, double t2C) {
		assert t1A < t1B && t1B < t1C;
		assert t2A < t2B && t2B < t2C;
		return t1A != t2A || t1B != t2B || t1C != t2C;
	}

	@UniqueTruePoint(
		implicant = "a",
		predicate = "a + b + c",
		dnf = "a + b + c",

		valuations = {
			@Valuation(clause = 'a', valuation = true),
			@Valuation(clause = 'b', valuation = false),
			@Valuation(clause = 'c', valuation = false)
		}
	)
	@Test
	public void FirstTest() {
		assertTrue(FirstPredicate(1,2,3,3,4,4));
	}

	@UniqueTruePoint(
		implicant = "c",
		predicate = "a + b + c",
		dnf = "a + b + c",

		valuations = {
			@Valuation(clause = 'c', valuation = true),
			@Valuation(clause = 'a', valuation = false),
			@Valuation(clause = 'b', valuation = false)
		}
	)
	@Test
	public void SecondTest() {
		assertTrue(FirstPredicate(1,1,2,2,3,4));
	}


	@UniqueTruePoint(
		implicant = "b",
		predicate = "a + b + c",
		dnf = "a + b + c",

		valuations = {
			@Valuation(clause = 'b', valuation = true),
			@Valuation(clause = 'a', valuation = false),
			@Valuation(clause = 'c', valuation = false)
		}
	)
	@Test
	public void ThirdTest() {
		assertTrue(FirstPredicate(1,1,2,3,4,4));
	}

	@NearFalsePoint(
		implicant = "a",
		clause = 'a',
		predicate = "a + b + c",
		dnf = "a + b + c",
		valuations = {
			@Valuation(clause = 'a', valuation = false),
			@Valuation(clause = 'b', valuation = false),
			@Valuation(clause = 'c', valuation = false)
		}
	)

	@NearFalsePoint(
		implicant = "c",
		clause = 'c',
		predicate = "a + b + c",
		dnf = "a + b + c",
		valuations = {
			@Valuation(clause = 'a', valuation = false),
			@Valuation(clause = 'b', valuation = false),
			@Valuation(clause = 'c', valuation = false)
		}
	)

	@NearFalsePoint(
		implicant = "b",
		clause = 'b',
		predicate = "a + b + c",
		dnf = "a + b + c",

		valuations = {
			@Valuation(clause = 'a', valuation = false),
			@Valuation(clause = 'b', valuation = false),
			@Valuation(clause = 'c', valuation = false)
		}
	)

	@Test
	public void FourthTest() {
		assertFalse(FirstPredicate(1,1,2,2,3,3));
	}



//	  Predicate: d + e ...  Order d, e
//
//	  d:
//	   CC : one test from {TT, TF} and one test from {FT, FF}
//	   CACC : e should be false -> {TF, FF}
//	  e:
//	  	CC : one test from {TT, FT} and one test from {TF, FF}
//	 	CACC : d should be false -> {FT, FF}
//
//	  CC :   {TT, FF} ... CACC : {FT, TF, FF}
//

	public boolean SecondPredicate(double t1A, double t1B, double t1C) {
		assert t1A < t1B && t1B < t1C;
		return t1A + t1B < t1C || t1A < 0;
	}

	@ClauseCoverage(
		predicate = "d + e",
		valuations = {
			@Valuation(clause = 'd', valuation = true),
			@Valuation(clause = 'e', valuation = true)
		}
	)
	@Test
	public void FifthTest() {
		assertTrue(SecondPredicate(-1, 2, 4));
	}

	@CACC(
		predicate = "d + e",
		majorClause = 'd',
		valuations = {
			@Valuation(clause = 'd', valuation = true),
			@Valuation(clause = 'e', valuation = false)
		},
		predicateValue = true
	)
	@Test
	public void SixthTest() {
		// Infeasible
	}

	@CACC(
		predicate = "d + e",
		majorClause = 'e',
		valuations = {
			@Valuation(clause = 'e', valuation = true),
			@Valuation(clause = 'd', valuation = false)
		},
		predicateValue = true
	)
	@Test
	public void SeventhTest() {
		assertTrue(SecondPredicate(1, 2, 4));
	}

	@ClauseCoverage(
		predicate = "d + e",
		valuations = {
			@Valuation(clause = 'd', valuation = false),
			@Valuation(clause = 'e', valuation = false)
		}
	)
	@CACC(
		predicate = "d + e",
		majorClause = 'e',
		valuations = {
			@Valuation(clause = 'd', valuation = false),
			@Valuation(clause = 'e', valuation = false)
		},
		predicateValue = false
	)

	@CACC(
		predicate = "d + e",
		majorClause = 'd',
		valuations = {
			@Valuation(clause = 'd', valuation = false),
			@Valuation(clause = 'e', valuation = false)
		},
		predicateValue = false
	)
	@Test
	public void EighthTest() {
		assertFalse(SecondPredicate(3, 4, 5));
	}

//	  Consider predicate: ab + cd
//	  First we find CUTPNFP
//	  Implicants: {ab, cd}
//	  ab: UTP:{TTFT}, NFP: a -> {FTFT} and b -> {TFFT}
//	  cd: UTP:{FTTT} , NFP: c -> {FTFT} and d -> {FTFT}
//
//	  CUTPNFP: {TTFT, FTTT, FTFT, TFFT, FTTF}
//
//	  Now we find UTP
//	  We must also consider ~f:  ~f = ~a~c + ~a~d + ~b~c + ~b~d
//	  Implicants: {ab, cd, ~a~c, ~a~d, ~b~c, ~b~d}
//
//	  We have 6 implicants, so we need 6 tegit sts for UTPC but we have 5 tests in CUTPNFP, so it can't subsume UTPC.

	private static boolean questionTwo(boolean a, boolean b, boolean c, boolean d, boolean e) {
		boolean predicate = false;
		predicate = (a && b) || (c && d);
		return predicate;
	}
}
