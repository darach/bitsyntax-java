/*
 * A bit syntax representation inspired by Erlang's bit syntax suitable for
 * describe binary packet structures suitable for compilation / code generation.
 *
 */

grammar BitSyntax;

@header {
// Copyright (c) 2013 Darach Ennis < darach at gmail dot com >.
//
// Permission is hereby granted, free of charge, to any person obtaining a
// copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to permit
// persons to whom the Software is furnished to do so, subject to the
// following conditions:  
//
// The above copyright notice and this permission notice shall be included
// in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
// OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
// NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
// DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
// OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
// USE OR OTHER DEALINGS IN THE SOFTWARE.


// package io.darach.bitsyntax;

}

// A binary is a sequence of element segments
binary
    : BO segments? BC
    ;

// Segments are comma delimited
segments
    :  segment (CM segment)*
    ;

segment
    : QS | (NM | ID) size? specifiers?
    ;

size 
    : CN NM | CN ID
    ;

specifiers
    : FS specifier (DS specifier)*
    ;
    
specifier 
    : (LE | BE | ST | UT | IT | BT | FT | DB) | unit
    ;

unit 
    : 'unit' CN NM
    ;

LE: 'little' ;
BE: 'big';
ST: 'signed';
UT: 'unsigned';
IT: 'integer';
BT: 'binary';
FT: 'float';
DB: 'double';

QS: '"' ( ES | ~('\\'|'"') )* '"';
fragment ES: '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\') | UES | OES;
fragment UES: '\\' 'u' HX HX HX HX;
fragment OES: '\\' ('0'..'3') ('0'..'7') ('0'..'7') | '\\' ('0'..'7') ('0'..'7') | '\\' ('0'..'7');

//NM: '0' | '1'..'9' ('0'..'9')*;
NM: IntegerLiteral;
ID : Identifier;
fragment
HX: ('0'..'9' | 'a'..'f' | 'A'..'F');

BO : '<<' ;
BC : '>>' ;

DS : '-' ;
FS : '/' ;
CM : ',' ;
CN : ':' ;

WS : [ \n\t\r]+ -> skip;

//
// The following productions copied from the Java.g4 grammar and are (C) 2012 Terence Parr
//

Identifier 
    :   Letter (Letter|JavaIDDigit)*
    ;

fragment
Letter
    :  '\u0024' |               // $
       '\u0041'..'\u005a' |     // A-Z
       '\u005f' |               // _  
       '\u0061'..'\u007a' |     // a-z
       '\u00c0'..'\u00d6' |     // Latin Capital Letter A with grave - Latin Capital letter O with diaeresis
       '\u00d8'..'\u00f6' |     // Latin Capital letter O with stroke - Latin Small Letter O with diaeresis
       '\u00f8'..'\u00ff' |     // Latin Small Letter O with stroke - Latin Small Letter Y with diaeresis
       '\u0100'..'\u1fff' |     // Latin Capital Letter A with macron - Latin Small Letter O with stroke and acute
       '\u3040'..'\u318f' |     // Hiragana
       '\u3300'..'\u337f' |     // CJK compatibility
       '\u3400'..'\u3d2d' |     // CJK compatibility
       '\u4e00'..'\u9fff' |     // CJK compatibility
       '\uf900'..'\ufaff'       // CJK compatibility
    ;

fragment
JavaIDDigit
    :  '\u0030'..'\u0039' |     // 0-9
       '\u0660'..'\u0669' |     // Arabic-Indic Digit 0-9
       '\u06f0'..'\u06f9' |     // Extended Arabic-Indic Digit 0-9
       '\u0966'..'\u096f' |     // Devanagari 0-9
       '\u09e6'..'\u09ef' |     // Bengali 0-9
       '\u0a66'..'\u0a6f' |     // Gurmukhi 0-9
       '\u0ae6'..'\u0aef' |     // Gujarati 0-9
       '\u0b66'..'\u0b6f' |     // Oriya 0-9
       '\u0be7'..'\u0bef' |     // Tami 0-9
       '\u0c66'..'\u0c6f' |     // Telugu 0-9
       '\u0ce6'..'\u0cef' |     // Kannada 0-9
       '\u0d66'..'\u0d6f' |     // Malayala 0-9
       '\u0e50'..'\u0e59' |     // Thai 0-9
       '\u0ed0'..'\u0ed9' |     // Lao 0-9
       '\u1040'..'\u1049'       // Myanmar 0-9?
   ;

NumericLiteral
    :   IntegerLiteral
    |   FloatingPointLiteral
    ;
    
TextLiteral
	:	CharacterLiteral
    |   StringLiteral
    ;
    
    
//    |   BooleanLiteral
//    |   'null'
//    ;
    
// §3.10.1 Integer Literals

IntegerLiteral
    :   DecimalIntegerLiteral
    |   HexIntegerLiteral
    |   OctalIntegerLiteral
    |   BinaryIntegerLiteral
    ;

fragment
DecimalIntegerLiteral
    :   DecimalNumeral IntegerTypeSuffix?
    ;

fragment
HexIntegerLiteral
    :   HexNumeral IntegerTypeSuffix?
    ;

fragment
OctalIntegerLiteral
    :   OctalNumeral IntegerTypeSuffix?
    ;

fragment
BinaryIntegerLiteral
    :   BinaryNumeral IntegerTypeSuffix?
    ;

fragment
IntegerTypeSuffix
    :   [lL]
    ;

fragment
DecimalNumeral
    :   '0'
    |   NonZeroDigit (Digits? | Underscores Digits)
    ;

fragment
Digits
    :   Digit (DigitOrUnderscore* Digit)?
    ;

fragment
Digit
    :   '0'
    |   NonZeroDigit
    ;

fragment
NonZeroDigit
    :   [1-9]
    ;

fragment
DigitOrUnderscore
    :   Digit
    |   '_'
    ;

fragment
Underscores
    :   '_'+
    ;

fragment
HexNumeral
    :   '0' [xX] HexDigits
    ;

fragment
HexDigits
    :   HexDigit (HexDigitOrUnderscore* HexDigit)?
    ;

fragment
HexDigit
    :   [0-9a-fA-F]
    ;

fragment
HexDigitOrUnderscore
    :   HexDigit
    |   '_'
    ;

fragment
OctalNumeral
    :   '0' Underscores? OctalDigits
    ;

fragment
OctalDigits
    :   OctalDigit (OctalDigitOrUnderscore* OctalDigit)?
    ;

fragment
OctalDigit
    :   [0-7]
    ;

fragment
OctalDigitOrUnderscore
    :   OctalDigit
    |   '_'
    ;

fragment
BinaryNumeral
    :   '0' [bB] BinaryDigits
    ;

fragment
BinaryDigits
    :   BinaryDigit (BinaryDigitOrUnderscore* BinaryDigit)?
    ;

fragment
BinaryDigit
    :   [01]
    ;

fragment
BinaryDigitOrUnderscore
    :   BinaryDigit
    |   '_'
    ;

// §3.10.2 Floating-Point Literals

FloatingPointLiteral
    :   DecimalFloatingPointLiteral
    |   HexadecimalFloatingPointLiteral
    ;

fragment
DecimalFloatingPointLiteral
    :   Digits '.' Digits? ExponentPart? FloatTypeSuffix?
    |   '.' Digits ExponentPart? FloatTypeSuffix?
    |   Digits ExponentPart FloatTypeSuffix?
    |   Digits FloatTypeSuffix
    ;

fragment
ExponentPart
    :   ExponentIndicator SignedInteger
    ;

fragment
ExponentIndicator
    :   [eE]
    ;

fragment
SignedInteger
    :   Sign? Digits
    ;

fragment
Sign
    :   [+-]
    ;

fragment
FloatTypeSuffix
    :   [fFdD]
    ;

fragment
HexadecimalFloatingPointLiteral
    :   HexSignificand BinaryExponent FloatTypeSuffix?
    ;

fragment
HexSignificand
    :   HexNumeral '.'?
    |   '0' [xX] HexDigits? '.' HexDigits
    ;

fragment
BinaryExponent
    :   BinaryExponentIndicator SignedInteger
    ;

fragment
BinaryExponentIndicator
    :   [pP]
    ;

// §3.10.3 Boolean Literals

BooleanLiteral
    :   'true'
    |   'false'
    ;

// §3.10.4 Character Literals

CharacterLiteral
    :   '\'' SingleCharacter '\''
    |   '\'' EscapeSequence '\''
    ;

fragment
SingleCharacter
    :   ~['\\]
    ;

// §3.10.5 String Literals

StringLiteral
    :   '"' StringCharacters? '"'
    ;

fragment
StringCharacters
    :   StringCharacter+
    ;

fragment
StringCharacter
    :   ~["\\]
    |   EscapeSequence
    ;

// §3.10.6 Escape Sequences for Character and String Literals

fragment
EscapeSequence
    :   '\\' [btnfr"'\\]
    |   OctalEscape
    |   UnicodeEscape
    ;

fragment
OctalEscape
    :   '\\' OctalDigit
    |   '\\' OctalDigit OctalDigit
    |   '\\' ZeroToThree OctalDigit OctalDigit
    ;

fragment
UnicodeEscape
    :   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;

fragment
ZeroToThree
    :   [0-3]
    ;

// §3.10.7 The Null Literal

NullLiteral
    :   'null'
    ;
    