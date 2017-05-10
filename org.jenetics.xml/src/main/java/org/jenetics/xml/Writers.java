/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.xml;

import static java.util.Objects.requireNonNull;
import static org.jenetics.xml.stream.Writer.attr;
import static org.jenetics.xml.stream.Writer.elem;
import static org.jenetics.xml.stream.Writer.elems;
import static org.jenetics.xml.stream.Writer.text;

import java.io.OutputStream;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.xml.stream.XMLStreamException;

import org.jenetics.BoundedGene;
import org.jenetics.Chromosome;
import org.jenetics.Gene;
import org.jenetics.xml.stream.AutoCloseableXMLStreamWriter;
import org.jenetics.xml.stream.Writer;
import org.jenetics.xml.stream.XML;

/**
 * This class contains static fields and methods, for creating chromosome- and
 * genotype writers for different gene types.
 *
 * <pre>{@code
 * final Writer<Genotype<BitGene> bch =
 *     Writers.Genotype.writer(Writers.BitChromosome.writer()));
 *
 * final Writer<Genotype<IntegerGene>> igw =
 *     Writers.Genotype.writer(Writers.IntegerChromosome.writer()));
 *
 * final Writer<Genotype<DoubleGene>> igw =
 *     Writers.Genotype.writer(Writers.DoubleChromosome.writer()));
 * }</pre>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Writers {
	private Writers() {}

	/**
	 * This class contains static writer methods for
	 * {@link org.jenetics.BitChromosome} objects.
	 * <p>
	 * <b>Writer code</b>
	 * <pre>{@code
	 * final BitChromosome value = BitChromosome.of(20, 0.5);
	 * try (AutoCloseableXMLStreamWriter xml = XML.writer(System.out, "    ")) {
	 *     Writers.BitChromosome.writer().write(value, xml);
	 * }
	 * }</pre>
	 *
	 * <b>XML output</b>
	 * <pre> {@code
	 * <bit-chromosome length="20" ones-probability="0.5">11100011101011001010</bit-chromosome>
	 * }</pre>
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version !__version__!
	 * @since !__version__!
	 */
	public static final class BitChromosome {
		private BitChromosome() {}

		static final String ROOT_NAME = "bit-chromosome";
		static final String LENGTH_NAME = "length";
		static final String ONES_PROBABILITY_NAME = "ones-probability";

		/**
		 * Return a {@link Writer} for {@link org.jenetics.BitChromosome}
		 * objects.
		 *
		 * @return a chromosome writer
		 */
		public static Writer<org.jenetics.BitChromosome> writer() {
			return elem(ROOT_NAME,
				attr(LENGTH_NAME).map(org.jenetics.BitChromosome::length),
				attr(ONES_PROBABILITY_NAME).map(ch -> ch.getOneProbability()),
				text().map(org.jenetics.BitChromosome::toCanonicalString)
			);
		}

		/**
		 * Write the given {@link org.jenetics.BitChromosome} to the given
		 * output stream.
		 *
		 * @param chromosome the bit-chromosome to write
		 * @param out the target output stream
		 * @throws XMLStreamException if an error occurs while writing the
		 *         chromosome
		 * @throws NullPointerException if one of the given arguments is
		 *         {@code null}
		 */
		public static void write(
			final org.jenetics.BitChromosome chromosome,
			final OutputStream out
		)
			throws XMLStreamException
		{
			requireNonNull(chromosome);
			requireNonNull(out);

			try (AutoCloseableXMLStreamWriter writer = XML.writer(out)) {
				writer().write(chromosome, writer);
			}
		}
	}


	/**
	 * This class contains static writer methods for
	 * {@link org.jenetics.CharacterChromosome} objects.
	 * <p>
	 * <b>Writer code</b>
	 * <pre>{@code
	 * final CharacterChromosome value = CharacterChromosome.of("ASDF", CharSeq.of("A-Z"));
	 * try (AutoCloseableXMLStreamWriter xml = XML.writer(System.out, "    ")) {
	 *     Writers.CharacterChromosome.writer().write(value, xml);
	 * }
	 * }</pre>
	 *
	 * <b>XML output</b>
	 * <pre> {@code
	 * <character-chromosome length="4">
	 *     <valid-alleles>ABCDEFGHIJKLMNOPQRSTUVWXYZ<valid-alleles>
	 *     <alleles>ASDF</alleles>
	 * </character-chromosome>
	 * }</pre>
	 */
	public static final class CharacterChromosome {
		private CharacterChromosome() {}

		static final String ROOT_NAME = "character-chromosome";
		static final String LENGTH_NAME = "length";
		static final String VALID_ALLELES_NAME = "valid-alleles";
		static final String ALLELES_NAME = "alleles";

		/**
		 * Return a {@link Writer} for {@link org.jenetics.CharacterChromosome}
		 * objects.
		 *
		 * @return a chromosome writer
		 */
		public static Writer<org.jenetics.CharacterChromosome> writer() {
			return elem(ROOT_NAME,
				attr(LENGTH_NAME).map(org.jenetics.CharacterChromosome::length),
				elem(VALID_ALLELES_NAME,
					text().map(ch -> ch.getGene().getValidCharacters())),
				elem(ALLELES_NAME,
					text().map(org.jenetics.CharacterChromosome::toString))
			);
		}

		/**
		 * Write the given {@link org.jenetics.CharacterChromosome} to the given
		 * output stream.
		 *
		 * @param chromosome the chromosome to write
		 * @param out the target output stream
		 * @param indent the XML level indentation
		 * @throws XMLStreamException if an error occurs while writing the
		 *         chromosome
		 * @throws NullPointerException if the {@code chromosome} or output
		 *         stream is {@code null}
		 */
		public static void write(
			final org.jenetics.CharacterChromosome chromosome,
			final OutputStream out,
			final String indent
		)
			throws XMLStreamException
		{
			requireNonNull(chromosome);
			requireNonNull(out);

			try (AutoCloseableXMLStreamWriter writer = XML.writer(out, indent)) {
				writer().write(chromosome, writer);
			}
		}

		/**
		 * Write the given {@link org.jenetics.CharacterChromosome} to the given
		 * output stream.
		 *
		 * @param chromosome the chromosome to write
		 * @param out the target output stream
		 * @throws XMLStreamException if an error occurs while writing the
		 *         chromosome
		 * @throws NullPointerException if the {@code chromosome} or output
		 *         stream is {@code null}
		 */
		public static void write(
			final org.jenetics.CharacterChromosome chromosome,
			final OutputStream out
		)
			throws XMLStreamException
		{
			write(chromosome, out, null);
		}

	}

	/**
	 * This class contains static writer methods for
	 * {@link org.jenetics.BoundedChromosome} objects.
	 *
	 * <p>
	 * <b>XML template</b>
	 * <pre> {@code
	 * <root-name length="3">
	 *     <min>aaa</min>
	 *     <max>zzz</max>
	 *     <alleles>
	 *         <allele>iii</allele>
	 *         <allele>fff</allele>
	 *         <allele>ggg</allele>
	 *     </alleles>
	 * </root-name>
	 * }</pre>
	 */
	public static final class BoundedChromosome {
		private BoundedChromosome() {}

		static final String LENGTH_NAME = "length";
		static final String MIN_NAME = "min";
		static final String MAX_NAME = "max";
		static final String ALLELE_NAME = "allele";
		static final String ALLELES_NAME = "alleles";

		/**
		 * Create a bounded chromosome writer with the given configuration.
		 *
		 * @param rootName the name of the root element. E.g. {@code int-chromosome}
		 * @param alleleWriter the XML writer used for the alleles
		 * @param <A> the allele type
		 * @param <G> the bounded gene type
		 * @param <C> the bounded chromosome type
		 * @return a bounded chromosome XML writer
		 * @throws NullPointerException if one of the arguments is {@code null}
		 */
		public static <
			A extends Comparable<? super A>,
			G extends BoundedGene<A, G>,
			C extends org.jenetics.BoundedChromosome<A, G>
		>
		Writer<C> writer(
			final String rootName,
			final Writer<? super A> alleleWriter)
		{
			requireNonNull(rootName);
			requireNonNull(alleleWriter);

			return elem(rootName,
				attr(LENGTH_NAME).map(ch -> ch.length()),
				elem(MIN_NAME, alleleWriter.map(ch -> ch.getMin())),
				elem(MAX_NAME, alleleWriter.map(ch -> ch.getMax())),
				elem(ALLELES_NAME,
					elems(elem(ALLELE_NAME, alleleWriter))
						.map(ch -> ch.toSeq().map(G::getAllele))
				)
			);
		}
	}

	/**
	 * This class contains static writer methods for
	 * {@link org.jenetics.IntegerChromosome} objects.
	 * <p>
	 * <b>Writer code</b>
	 * <pre>{@code
	 * final IntegerChromosome value = IntegerChromosome
	 *     .of(Integer.MIN_VALUE, Integer.MAX_VALUE, 3);
	 * try (AutoCloseableXMLStreamWriter xml = XML.writer(System.out, "    ")) {
	 *     Writers.IntegerChromosome.writer().write(value, xml);
	 * }
	 * }</pre>
	 *
	 * <b>XML output</b>
	 * <pre> {@code
	 * <int-chromosome length="3">
	 *     <min>-2147483648</min>
	 *     <max>2147483647</max>
	 *     <alleles>
	 *         <allele>-1878762439</allele>
	 *         <allele>-957346595</allele>
	 *         <allele>-88668137</allele>
	 *     </alleles>
	 * </int-chromosome>
	 * }</pre>
	 */
	public static final class IntegerChromosome {
		private IntegerChromosome() {}

		static final String ROOT_NAME = "int-chromosome";

		/**
		 * Return the default integer allele writer for the
		 * {@code IntegerChromosome}.
		 *
		 * @return the default integer allele writer
		 */
		public static Writer<Integer> alleleWriter() {
			return text();
		}

		/**
		 * Return a {@link Writer} for {@link org.jenetics.IntegerChromosome}
		 * objects.
		 *
		 * @param alleleWriter the allele writer used for writing the integer
		 *        allele. Might be useful for using different integer
		 *        <i>encodings</i>.
		 * @return a chromosome writer
		 * @throws NullPointerException if the given {@code alleleWriter} is
		 *         {@code null}
		 */
		public static Writer<org.jenetics.IntegerChromosome>
		writer(final Writer<? super Integer> alleleWriter) {
			requireNonNull(alleleWriter);

			return BoundedChromosome.writer(ROOT_NAME, alleleWriter);
		}

		/**
		 * Return a {@link Writer} for {@link org.jenetics.IntegerChromosome}
		 * objects.
		 *
		 * @return a chromosome writer
		 */
		public static Writer<org.jenetics.IntegerChromosome> writer() {
			return writer(alleleWriter());
		}

		/**
		 * Write the given {@link org.jenetics.IntegerChromosome} to the given
		 * output stream.
		 *
		 * @param chromosome the chromosome to write
		 * @param out the target output stream
		 * @param indent the XML level indentation
		 * @throws XMLStreamException if an error occurs while writing the
		 *         chromosome
		 * @throws NullPointerException if the {@code chromosome} or output
		 *         stream is {@code null}
		 */
		public static void write(
			final org.jenetics.IntegerChromosome chromosome,
			final OutputStream out,
			final String indent
		)
			throws XMLStreamException
		{
			requireNonNull(chromosome);
			requireNonNull(out);

			try (AutoCloseableXMLStreamWriter writer = XML.writer(out, indent)) {
				writer().write(chromosome, writer);
			}
		}

		/**
		 * Write the given {@link org.jenetics.IntegerChromosome} to the given
		 * output stream.
		 *
		 * @param chromosome the chromosome to write
		 * @param out the target output stream
		 * @throws XMLStreamException if an error occurs while writing the
		 *         chromosome
		 * @throws NullPointerException if the {@code chromosome} or output
		 *         stream is {@code null}
		 */
		public static void write(
			final org.jenetics.IntegerChromosome chromosome,
			final OutputStream out
		)
			throws XMLStreamException
		{
			write(chromosome, out, null);
		}
	}

	/**
	 * This class contains static writer methods for
	 * {@link org.jenetics.LongChromosome} objects.
	 * <p>
	 * <b>Writer code</b>
	 * <pre>{@code
	 * final LongChromosome value = LongChromosome
	 *     .of(Long.MIN_VALUE, Long.MAX_VALUE, 3);
	 * try (AutoCloseableXMLStreamWriter xml = XML.writer(System.out, "    ")) {
	 *     Writers.LongChromosome.writer().write(value, xml);
	 * }
	 * }</pre>
	 *
	 * <b>XML output</b>
	 * <pre> {@code
	 * <long-chromosome length="3">
	 *     <min>-9223372036854775808</min>
	 *     <max>9223372036854775807</max>
	 *     <alleles>
	 *         <allele>-1345217698116542402</allele>
	 *         <allele>-7144755673073475303</allele>
	 *         <allele>6053786736809578435</allele>
	 *     </alleles>
	 * </long-chromosome>
	 * }</pre>
	 */
	public static final class LongChromosome {
		private LongChromosome() {}

		static final String ROOT_NAME = "long-chromosome";

		/**
		 * Return the default long allele writer for the
		 * {@code IntegerChromosome}.
		 *
		 * @return the default long allele writer
		 */
		public static Writer<Long> alleleWriter() {
			return Writer.text();
		}

		/**
		 * Return a {@link Writer} for {@link org.jenetics.LongChromosome}
		 * objects.
		 *
		 * @param alleleWriter the allele writer used for writing the long
		 *        allele. Might be useful for using different long
		 *        <i>encodings</i>.
		 * @return a chromosome writer
		 * @throws NullPointerException if the given {@code alleleWriter} is
		 *         {@code null}
		 */
		public static Writer<org.jenetics.LongChromosome>
		writer(final Writer<? super Long> alleleWriter) {
			return BoundedChromosome.writer(ROOT_NAME, alleleWriter);
		}

		/**
		 * Return a {@link Writer} for {@link org.jenetics.LongChromosome}
		 * objects.
		 *
		 * @return a chromosome writer
		 */
		public static Writer<org.jenetics.LongChromosome> writer() {
			return writer(alleleWriter());
		}

		/**
		 * Write the given {@link org.jenetics.LongChromosome} to the given
		 * output stream.
		 *
		 * @param chromosome the chromosome to write
		 * @param out the target output stream
		 * @param indent the XML level indentation
		 * @throws XMLStreamException if an error occurs while writing the
		 *         chromosome
		 * @throws NullPointerException if the {@code chromosome} or output
		 *         stream is {@code null}
		 */
		public static void write(
			final org.jenetics.LongChromosome chromosome,
			final OutputStream out,
			final String indent
		)
			throws XMLStreamException
		{
			requireNonNull(chromosome);
			requireNonNull(out);

			try (AutoCloseableXMLStreamWriter writer = XML.writer(out, indent)) {
				writer().write(chromosome, writer);
			}
		}

		/**
		 * Write the given {@link org.jenetics.LongChromosome} to the given
		 * output stream.
		 *
		 * @param chromosome the chromosome to write
		 * @param out the target output stream
		 * @throws XMLStreamException if an error occurs while writing the
		 *         chromosome
		 * @throws NullPointerException if the {@code chromosome} or output
		 *         stream is {@code null}
		 */
		public static void write(
			final org.jenetics.LongChromosome chromosome,
			final OutputStream out
		)
			throws XMLStreamException
		{
			write(chromosome, out, null);
		}
	}

	/**
	 * This class contains static writer methods for
	 * {@link org.jenetics.DoubleChromosome} objects.
	 * <p>
	 * <b>Writer code</b>
	 * <pre>{@code
	 * final DoubleChromosome value = DoubleChromosome.of(0.0, 1.0, 3);
	 * try (AutoCloseableXMLStreamWriter xml = XML.writer(System.out, "    ")) {
	 *     Writers.DoubleChromosome.writer().write(value, xml);
	 * }
	 * }</pre>
	 *
	 * <b>XML output</b>
	 * <pre> {@code
	 * <double-chromosome length="3">
	 *     <min>0.0</min>
	 *     <max>1.0</max>
	 *     <alleles>
	 *         <allele>0.27251556008507416</allele>
	 *         <allele>0.003140816229067145</allele>
	 *         <allele>0.43947528327497376</allele>
	 *     </alleles>
	 * </double-chromosome>
	 * }</pre>
	 */
	public static final class DoubleChromosome {
		private DoubleChromosome() {}

		static final String ROOT_NAME = "double-chromosome";

		/**
		 * Return the default double allele writer for the
		 * {@code DoubleChromosome}.
		 *
		 * @return the default double allele writer
		 */
		public static Writer<Double> alleleWriter() {
			return text().map(Object::toString);
		}

		/**
		 * Return a {@link Writer} for {@link org.jenetics.DoubleChromosome}
		 * objects.
		 *
		 * @param alleleWriter the allele writer used for writing the long
		 *        allele. Might be useful for using different long
		 *        <i>encodings</i>.
		 * @return a chromosome writer
		 * @throws NullPointerException if the given {@code alleleWriter} is
		 *         {@code null}
		 */
		public static Writer<org.jenetics.DoubleChromosome>
		writer(final Writer<? super Double> alleleWriter) {
			return BoundedChromosome.writer(ROOT_NAME, alleleWriter);
		}

		/**
		 * Return a {@link Writer} for {@link org.jenetics.DoubleChromosome}
		 * objects.
		 *
		 * @return a chromosome writer
		 */
		public static Writer<org.jenetics.DoubleChromosome> writer() {
			return writer(alleleWriter());
		}

		/**
		 * Write the given {@link org.jenetics.DoubleChromosome} to the given
		 * output stream.
		 *
		 * @param chromosome the chromosome to write
		 * @param out the target output stream
		 * @param indent the XML level indentation
		 * @throws XMLStreamException if an error occurs while writing the
		 *         chromosome
		 * @throws NullPointerException if the {@code chromosome} or output
		 *         stream is {@code null}
		 */
		public static void write(
			final org.jenetics.DoubleChromosome chromosome,
			final OutputStream out,
			final String indent
		)
			throws XMLStreamException
		{
			requireNonNull(chromosome);
			requireNonNull(out);

			try (AutoCloseableXMLStreamWriter writer = XML.writer(out, indent)) {
				writer().write(chromosome, writer);
			}
		}

		/**
		 * Write the given {@link org.jenetics.DoubleChromosome} to the given
		 * output stream.
		 *
		 * @param chromosome the chromosome to write
		 * @param out the target output stream
		 * @throws XMLStreamException if an error occurs while writing the
		 *         chromosome
		 * @throws NullPointerException if the {@code chromosome} or output
		 *         stream is {@code null}
		 */
		public static void write(
			final org.jenetics.DoubleChromosome chromosome,
			final OutputStream out
		)
			throws XMLStreamException
		{
			write(chromosome, out, null);
		}
	}

	/**
	 * This class contains static writer methods for
	 * {@link org.jenetics.PermutationChromosome} objects.
	 * <p>
	 * <b>Writer code</b>
	 * <pre>{@code
	 * final PermutationChromosome<Integer> value =
	 *     PermutationChromosome.ofInteger(5)
	 *
	 * final Writer<PermutationChromosome<Integer> writer =
	 *     Writers.PermutationChromosome.writer();
	 *
	 * try (AutoCloseableXMLStreamWriter xml = XML.writer(System.out, "    ")) {
	 *     Writers.PermutationChromosome.writer().write(value, xml);
	 * }
	 * }</pre>
	 *
	 * <b>XML output</b>
	 * <pre> {@code
	 * <permutation-chromosome length="5">
	 *     <valid-alleles>
	 *         <allele>0</allele>
	 *         <allele>1</allele>
	 *         <allele>2</allele>
	 *         <allele>3</allele>
	 *         <allele>4</allele>
	 *     </valid-alleles>
	 *     <order>2 1 3 5 4</order>
	 * </permutation-chromosome>
	 * }</pre>
	 */
	public static final class PermutationChromosome {
		private PermutationChromosome() {}

		static final String ROOT_NAME = "permutation-chromosome";
		static final String LENGTH_NAME = "length";
		static final String VALID_ALLELES_NAME = "valid-alleles";
		static final String ALLELE_NAME = "allele";
		static final String ORDER_NAME = "order";

		/**
		 * Create a writer for permutation-chromosomes. How to write the valid
		 * alleles is defined by the given {@link Writer}.
		 *
		 * @param alleleWriter the allele writer
		 * @param <A> the allele type
		 * @return a new permutation chromosome writer
		 * @throws NullPointerException if the given allele {@code writer} is
		 *         {@code null}
		 */
		public static <A> Writer<org.jenetics.PermutationChromosome<A>>
		writer(final Writer<? super A> alleleWriter) {
			return elem(ROOT_NAME,
				attr(LENGTH_NAME).map(org.jenetics.PermutationChromosome::length),
				elem(VALID_ALLELES_NAME,
					attr("type").map(PermutationChromosome::toAlleleTypeName),
					elems(elem(ALLELE_NAME, alleleWriter)).map(ch -> ch.getValidAlleles())
				),
				elem(ORDER_NAME, text())
					.map(ch -> ch.stream()
						.map(g -> Integer.toString(g.getAlleleIndex()))
						.collect(Collectors.joining(" ")))
			);
		}

		private static String toAlleleTypeName(
			final org.jenetics.PermutationChromosome<?> ch
		) {
			return ch.getGene().getAllele().getClass().getCanonicalName();
		}

		/**
		 * Create a writer for permutation-chromosomes. The valid alleles are
		 * serialized by calling the {@link Object#toString()} method. Calling
		 * this method is equivalent with:
		 * <pre>{@code
		 * final Writer<PermutationChromosome<Double> writer =
		 *     PermutationChromosome.write(text().map(Objects::toString));
		 * }</pre>
		 *
		 * Example output:
		 * <pre> {@code
		 * <permutation-chromosome length="15">
		 *     <valid-alleles type="java.lang.Double">
		 *         <allele>0.27251556008507416</allele>
		 *         <allele>0.003140816229067145</allele>
		 *         <allele>0.43947528327497376</allele>
		 *         <allele>0.10654807463069327</allele>
		 *         <allele>0.19696530915810317</allele>
		 *         <allele>0.7450003838065538</allele>
		 *         <allele>0.5594416969271359</allele>
		 *         <allele>0.02823782430152355</allele>
		 *         <allele>0.5741102315010789</allele>
		 *         <allele>0.4533651041367144</allele>
		 *         <allele>0.811148141800367</allele>
		 *         <allele>0.5710456351848858</allele>
		 *         <allele>0.30166768355230955</allele>
		 *         <allele>0.5455492865240272</allele>
		 *         <allele>0.21068427527733102</allele>
		 *     </valid-alleles>
		 *     <order>13 12 4 6 8 14 7 2 11 5 3 0 9 10 1</order>
		 * </permutation-chromosome>
		 * }</pre>
		 *
		 * @param <A> the allele type
		 * @return a new permutation chromosome writer
		 */
		public static <A> Writer<org.jenetics.PermutationChromosome<A>> writer() {
			return writer(text());
		}

		/**
		 * Write the given {@link org.jenetics.PermutationChromosome} to the
		 * given output stream.
		 *
		 * @param <A> the allele type
		 * @param chromosome the chromosome to write
		 * @param out the target output stream
		 * @param indent the XML level indentation
		 * @throws XMLStreamException if an error occurs while writing the
		 *         chromosome
		 * @throws NullPointerException if the {@code chromosome} or output
		 *         stream is {@code null}
		 */
		public static <A> void write(
			final org.jenetics.PermutationChromosome<A> chromosome,
			final OutputStream out,
			final String indent
		)
			throws XMLStreamException
		{
			requireNonNull(chromosome);
			requireNonNull(out);

			try (AutoCloseableXMLStreamWriter writer = XML.writer(out, indent)) {
				PermutationChromosome.<A>writer().write(chromosome, writer);
			}
		}

		/**
		 * Write the given {@link org.jenetics.PermutationChromosome} to the
		 * given output stream.
		 *
		 * @param <A> the allele type
		 * @param chromosome the chromosome to write
		 * @param alleleWriter the allele writer of the permutation chromosome
		 * @param out the target output stream
		 * @param indent the XML level indentation
		 * @throws XMLStreamException if an error occurs while writing the
		 *         chromosome
		 * @throws NullPointerException if the {@code chromosome} or output
		 *         stream is {@code null}
		 */
		public static <A> void write(
			final org.jenetics.PermutationChromosome<A> chromosome,
			final Writer<? super A> alleleWriter,
			final OutputStream out,
			final String indent
		)
			throws XMLStreamException
		{
			requireNonNull(chromosome);
			requireNonNull(alleleWriter);
			requireNonNull(out);

			try (AutoCloseableXMLStreamWriter writer = XML.writer(out, indent)) {
				PermutationChromosome.<A>writer(alleleWriter)
					.write(chromosome, writer);
			}
		}

		/**
		 * Write the given {@link org.jenetics.PermutationChromosome} to the
		 * given output stream.
		 *
		 * @param <A> the allele type
		 * @param chromosome the chromosome to write
		 * @param out the target output stream
		 * @throws XMLStreamException if an error occurs while writing the
		 *         chromosome
		 * @throws NullPointerException if the {@code chromosome} or output
		 *         stream is {@code null}
		 */
		public static <A> void write(
			final org.jenetics.PermutationChromosome<A> chromosome,
			final OutputStream out
		)
			throws XMLStreamException
		{
			write(chromosome, out, null);
		}

		/**
		 * Write the given {@link org.jenetics.PermutationChromosome} to the
		 * given output stream.
		 *
		 * @param <A> the allele type
		 * @param chromosome the chromosome to write
		 * @param alleleWriter the allele writer used to write the chromosome
		 *         alleles
		 * @param out the target output stream
		 * @throws XMLStreamException if an error occurs while writing the
		 *         chromosome
		 * @throws NullPointerException if the {@code chromosome} or output
		 *         stream is {@code null}
		 */
		public static <A> void write(
			final org.jenetics.PermutationChromosome<A> chromosome,
			final Writer<? super A> alleleWriter,
			final OutputStream out
		)
			throws XMLStreamException
		{
			write(chromosome, alleleWriter, out, null);
		}

	}

	/**
	 * This class contains static writer methods for
	 * {@link org.jenetics.Genotype} objects.
	 * <p>
	 * <b>Writer code</b>
	 * <pre>{@code
	 * final Genotype<DoubleGene> gt = Genotype.of(
	 *     DoubleChromosome.of(0.0, 1.0, 3),
	 *     DoubleChromosome.of(0.0, 1.0, 2)
	 * );
	 * final Writer<Genotype<DoubleGene>> writer =
	 *     Writers.Genotype.writer(Writers.DoubleChromosome.writer());
	 *
	 * try (AutoCloseableXMLStreamWriter xml = XML.writer(System.out, "    ")) {
	 *     writer.write(value, xml);
	 * }
	 * }</pre>
	 *
	 * <b>XML output</b>
	 * <pre> {@code
	 * <genotype length="2" ngenes="5">
	 *     <double-chromosome length="3">
	 *         <min>0.0</min>
	 *         <max>1.0</max>
	 *         <alleles>
	 *             <allele>0.27251556008507416</allele>
	 *             <allele>0.003140816229067145</allele>
	 *             <allele>0.43947528327497376</allele>
	 *         </alleles>
	 *     </double-chromosome>
	 *     <double-chromosome length="2">
	 *         <min>0.0</min>
	 *         <max>1.0</max>
	 *         <alleles>
	 *             <allele>0.4026521545744768</allele>
	 *             <allele>0.36137605952663554</allele>
	 *         <alleles>
	 *     </double-chromosome>
	 * </genotype>
	 * }</pre>
	 */
	public static final class Genotype {
		private Genotype() {}

		static final String ROOT_NAME = "genotype";
		static final String LENGTH_NAME = "length";
		static final String NGENES_NAME = "ngenes";

		/**
		 * Create a writer for genotypes of arbitrary chromosomes. How to write the
		 * genotypes chromosomes is defined by the given {@link Writer}.
		 *
		 * @param writer the chromosome writer
		 * @param <A> the allele type
		 * @param <G> the gene type
		 * @param <C> the chromosome type
		 * @return a new genotype writer
		 * @throws NullPointerException if the given chromosome {@code writer} is
		 *         {@code null}
		 */
		public static <
			A,
			G extends Gene<A, G>,
			C extends Chromosome<G>
		>
		Writer<org.jenetics.Genotype<G>> writer(final Writer<? super C> writer) {
			return elem(
				ROOT_NAME,
				attr(LENGTH_NAME).map(org.jenetics.Genotype<G>::length),
				attr(NGENES_NAME).map(org.jenetics.Genotype<G>::getNumberOfGenes),
				elems(writer).map(gt -> cast(gt.toSeq()))
			);
		}

		@SuppressWarnings("unchecked")
		private static <A, B> B cast(final A value) {
			return (B)value;
		}

		/**
		 * Write the given {@link org.jenetics.Genotype} to the given output
		 * stream.
		 *
		 * @param <A> the allele type
		 * @param <G> the gene type
		 * @param <C> the chromosome type
		 * @param genotype the genotype to write
		 * @param chromosomeWriter the chromosome writer used to write the
		 *        genotypes
		 * @param out the target output stream
		 * @param indent the XML level indentation
		 * @throws XMLStreamException if an error occurs while writing the
		 *         chromosome
		 * @throws NullPointerException if the one of the arguments is
		 *         {@code null}
		 */
		public static <
			A,
			G extends Gene<A, G>,
			C extends Chromosome<G>
		>
		void write(
			final org.jenetics.Genotype<G> genotype,
			final Writer<? super C> chromosomeWriter,
			final OutputStream out,
			final String indent
		)
			throws XMLStreamException
		{
			requireNonNull(genotype);
			requireNonNull(chromosomeWriter);
			requireNonNull(out);

			try (AutoCloseableXMLStreamWriter writer = XML.writer(out, indent)) {
				Genotype.<A, G, C>writer(chromosomeWriter).write(genotype, writer);
			}
		}

		/**
		 * Write the given {@link org.jenetics.Genotype} to the given output
		 * stream.
		 *
		 * @param <A> the allele type
		 * @param <G> the gene type
		 * @param <C> the chromosome type
		 * @param genotype the genotype to write
		 * @param chromosomeWriter the chromosome writer used to write the
		 *        genotypes
		 * @param out the target output stream
		 * @throws XMLStreamException if an error occurs while writing the
		 *         chromosome
		 * @throws NullPointerException if the one of the arguments is
		 *         {@code null}
		 */
		public static <
			A,
			G extends Gene<A, G>,
			C extends Chromosome<G>
		>
		void write(
			final org.jenetics.Genotype<G> genotype,
			final Writer<? super C> chromosomeWriter,
			final OutputStream out
		)
			throws XMLStreamException
		{
			requireNonNull(genotype);
			requireNonNull(chromosomeWriter);
			requireNonNull(out);

			try (AutoCloseableXMLStreamWriter writer = XML.writer(out)) {
				Genotype.<A, G, C>writer(chromosomeWriter).write(genotype, writer);
			}
		}

	}

	/**
	 * This class contains static writer methods for
	 * {@link org.jenetics.Genotype} objects.
	 * <p>
	 * <b>Writer code</b>
	 * <pre>{@code
	 * final Genotype<DoubleGene> gt = Genotype.of(
	 *     DoubleChromosome.of(0.0, 1.0, 3),
	 *     DoubleChromosome.of(0.0, 1.0, 2)
	 * );
	 *
	 * final Writer<Collection<Genotype<DoubleGene>>> writer =
	 *     Writers.Genotypes.writer(Writers.DoubleChromosome.writer());
	 *
	 * try (AutoCloseableXMLStreamWriter xml = XML.writer(System.out, "    ")) {
	 *     writer.write(asList(value), xml);
	 * }
	 * }</pre>
	 *
	 * <pre> {@code
	 * <genotypes length="1">
	 *     <genotype length="2" ngenes="5">
	 *         <double-chromosome length="3">
	 *             <min>0.0</min>
	 *             <max>1.0</max>
	 *             <alleles>
	 *                 <allele>0.27251556008507416</allele>
	 *                 <allele>0.003140816229067145</allele>
	 *                 <allele>0.43947528327497376</allele>
	 *             </alleles>
	 *         </double-chromosome>
	 *         <double-chromosome length="2">
	 *             <min>0.0</min>
	 *             <max>1.0</max>
	 *             <alleles>
	 *                 <allele>0.4026521545744768</allele>
	 *                 <allele>0.36137605952663554</allele>
	 *             <alleles>
	 *         </double-chromosome>
	 *     </genotype>
	 * </genotypes>
	 * }</pre>
	 */
	public static final class Genotypes {
		private Genotypes() {}

		static final String ROOT_NAME = "genotypes";
		static final String LENGTH_NAME = "length";

		/**
		 * Create a writer for genotypes of arbitrary chromosomes. How to write the
		 * genotypes chromosomes is defined by the given {@link Writer}. The
		 * following writer allows to write double-gene chromosomes:
		 * <pre>{@code
		 * final Writer<Collection<Genotype<DoubleGene>>> writer =
		 *     Writers.Genotypes.writer(Writers.DoubleChromosome.writer());
		 * }</pre>
		 *
		 * @param writer the chromosome writer
		 * @param <A> the allele type
		 * @param <G> the gene type
		 * @param <C> the chromosome type
		 * @return a new genotype writer
		 * @throws NullPointerException if the given chromosome {@code writer} is
		 *         {@code null}
		 */
		public static <
			A,
			G extends Gene<A, G>,
			C extends Chromosome<G>
		>
		Writer<Collection<org.jenetics.Genotype<G>>>
		writer(final Writer<? super C> writer) {
			return elem(
				ROOT_NAME,
				attr(LENGTH_NAME).map(Collection::size),
				elems(Genotype.writer(writer))
			);
		}

		/**
		 * Write the given {@link org.jenetics.Genotype} to the given output
		 * stream.
		 *
		 * @param <A> the allele type
		 * @param <G> the gene type
		 * @param <C> the chromosome type
		 * @param genotypes the genotypes to write
		 * @param chromosomeWriter the chromosome writer used to write the
		 *        genotypes
		 * @param out the target output stream
		 * @param indent the XML level indentation
		 * @throws XMLStreamException if an error occurs while writing the
		 *         chromosome
		 * @throws NullPointerException if the one of the arguments is
		 *         {@code null}
		 */
		public static <
			A,
			G extends Gene<A, G>,
			C extends Chromosome<G>
		>
		void write(
			final Collection<org.jenetics.Genotype<G>> genotypes,
			final Writer<? super C> chromosomeWriter,
			final OutputStream out,
			final String indent
		)
			throws XMLStreamException
		{
			requireNonNull(genotypes);
			requireNonNull(chromosomeWriter);
			requireNonNull(out);

			try (AutoCloseableXMLStreamWriter writer = XML.writer(out, indent)) {
				Genotypes.<A, G, C>writer(chromosomeWriter).write(genotypes, writer);
			}
		}

		/**
		 * Write the given {@link org.jenetics.Genotype} to the given output
		 * stream.
		 *
		 * @param <A> the allele type
		 * @param <G> the gene type
		 * @param <C> the chromosome type
		 * @param genotypes the genotypes to write
		 * @param chromosomeWriter the chromosome writer used to write the
		 *        genotypes
		 * @param out the target output stream
		 * @throws XMLStreamException if an error occurs while writing the
		 *         chromosome
		 * @throws NullPointerException if the one of the arguments is
		 *         {@code null}
		 */
		public static <
			A,
			G extends Gene<A, G>,
			C extends Chromosome<G>
		>
		void write(
			final Collection<org.jenetics.Genotype<G>> genotypes,
			final Writer<? super C> chromosomeWriter,
			final OutputStream out
		)
			throws XMLStreamException
		{
			requireNonNull(genotypes);
			requireNonNull(chromosomeWriter);
			requireNonNull(out);

			try (AutoCloseableXMLStreamWriter writer = XML.writer(out)) {
				Genotypes.<A, G, C>writer(chromosomeWriter).write(genotypes, writer);
			}
		}

	}


	/**
	 * Write the given {@link org.jenetics.Genotype} to the given output
	 * stream.
	 *
	 * @see Genotypes#write(Collection, Writer, OutputStream)
	 *
	 * @param <A> the allele type
	 * @param <G> the gene type
	 * @param <C> the chromosome type
	 * @param genotypes the genotypes to write
	 * @param chromosomeWriter the chromosome writer used to write the
	 *        genotypes
	 * @param out the target output stream
	 * @throws XMLStreamException if an error occurs while writing the
	 *         chromosome
	 * @throws NullPointerException if the one of the arguments is
	 *         {@code null}
	 */
	public static <
		A,
		G extends Gene<A, G>,
		C extends Chromosome<G>
	>
	void write(
		final Collection<org.jenetics.Genotype<G>> genotypes,
		final Writer<? super C> chromosomeWriter,
		final OutputStream out
	)
		throws XMLStreamException
	{
		Genotypes.write(genotypes, chromosomeWriter, out);
	}

}
