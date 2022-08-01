library IEEE;
        use IEEE.std_logic_1164.all; -- import std_logic types
        --use IEEE.std_logic_arith.all; -- import add/sub of std_logic_vector
        --use IEEE.std_logic_unsigned.all;
        --use IEEE.std_logic_signed.all;
        use IEEE.math_real.all;
        use IEEE.numeric_std.all; -- for type conversion to_unsigned

        --library STD;
        --use STD.textio.all;

        --------------------------------------------------------------------------------
        --!@file: TB_mult_algorithmic.vhd
        --!@brief: testbench for the algorithmic multiplier description
        --!...
        --
        --!@author: Tobias Koal(TK)
        --!@revision info :
        -- last modification by tkoal(TK)
        -- Mon Apr 13 14:27:02 CEST 2015
        --------------------------------------------------------------------------------

        -- entity description

        entity TB_mult_algorithmic is
        end entity;

        -- architecture description

        architecture testbench of TB_mult_algorithmic is

        component mult_algorithmic
        port(
        a   : in   unsigned(19 downto 0);
        b   : in   unsigned(19 downto 0);
        z   : out  unsigned(39 downto 0);
        );
        end component;


        -- CONSTANTS (upper case only!)


        -- SIGNALS (lower case only!)

        signal a,b : unsigned(19 downto 0) := (others => '0');
        signal z : unsigned(39 downto 0) := (others => '0');
        signal test : unsigned(39 downto 0) := (others => '0');


        begin

        -- this is your algorithmic multiplier description
        tb_component : entity work. mult_algorithmic(algorithmic_description)
        port map (
        a => a,
        b => b,
        z => z
        );

        -- create a stimulus process here!
        stimul : process
        begin
        a <= to_unsigned(3, 20) after 1 ns;
        b <= to_unsigned(5, 20) after 3 ns;
        a <= to_unsigned(1, 20) after 4 ns;
        b <= to_unsigned(2, 20) after 4 ns;
        a <= to_unsigned(5, 20) after 5 ns;
        b <= to_unsigned(6, 20) after 5 ns;
        a <= to_unsigned(8, 20) after 6 ns;
        b <= to_unsigned(4, 20) after 6 ns;
        a <= to_unsigned(9, 20) after 7 ns;
        b <= to_unsigned(7, 20) after 7 ns;
        wait for 5 ns;
        end process;


        -- create a golden device here!
        g <= a * b;


        -- create a compare process here!
        assert(g = z)
        report "mismatch"
        severity warning;




        end testbench;
