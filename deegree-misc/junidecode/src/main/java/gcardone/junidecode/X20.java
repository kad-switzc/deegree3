/*
 * Copyright (C) 2015 Giuseppe Cardone <ippatsuman@gmail.com>
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
 */
package gcardone.junidecode;

/**
 * Character map for Unicode characters with codepoint U+20xx.
 * @author Giuseppe Cardone
 * @version 0.1
 */
class X20 {

    public static final String[] map = new String[]{
        " ", // 0x00
        " ", // 0x01
        " ", // 0x02
        " ", // 0x03
        " ", // 0x04
        " ", // 0x05
        " ", // 0x06
        " ", // 0x07
        " ", // 0x08
        " ", // 0x09
        " ", // 0x0a
        " ", // 0x0b
        "", // 0x0c
        "", // 0x0d
        "", // 0x0e
        "", // 0x0f
        "-", // 0x10
        "-", // 0x11
        "-", // 0x12
        "-", // 0x13
        "--", // 0x14
        "--", // 0x15
        "||", // 0x16
        "_", // 0x17
        "\'", // 0x18
        "\'", // 0x19
        ",", // 0x1a
        "\'", // 0x1b
        "\"", // 0x1c
        "\"", // 0x1d
        ",,", // 0x1e
        "\"", // 0x1f
        "+", // 0x20
        "++", // 0x21
        "*", // 0x22
        "*>", // 0x23
        ".", // 0x24
        "..", // 0x25
        "...", // 0x26
        ".", // 0x27
        new String("" + (char) 0x0a), // 0x28
        new String("" + (char) 0x0a + (char) 0x0a), // 0x29
        "", // 0x2a
        "", // 0x2b
        "", // 0x2c
        "", // 0x2d
        "", // 0x2e
        " ", // 0x2f
        "%0", // 0x30
        "%00", // 0x31
        "\'", // 0x32
        "\'\'", // 0x33
        "\'\'\'", // 0x34
        "`", // 0x35
        "``", // 0x36
        "```", // 0x37
        "^", // 0x38
        "<", // 0x39
        ">", // 0x3a
        "*", // 0x3b
        "!!", // 0x3c
        "!?", // 0x3d
        "-", // 0x3e
        "_", // 0x3f
        "-", // 0x40
        "^", // 0x41
        "***", // 0x42
        "--", // 0x43
        "/", // 0x44
        "-[", // 0x45
        "]-", // 0x46
        "[?]", // 0x47
        "?!", // 0x48
        "!?", // 0x49
        "7", // 0x4a
        "PP", // 0x4b
        "(]", // 0x4c
        "[)", // 0x4d
        "[?]", // 0x4e
        "[?]", // 0x4f
        "[?]", // 0x50
        "[?]", // 0x51
        "[?]", // 0x52
        "[?]", // 0x53
        "[?]", // 0x54
        "[?]", // 0x55
        "[?]", // 0x56
        "[?]", // 0x57
        "[?]", // 0x58
        "[?]", // 0x59
        "[?]", // 0x5a
        "[?]", // 0x5b
        "[?]", // 0x5c
        "[?]", // 0x5d
        "[?]", // 0x5e
        "[?]", // 0x5f
        "[?]", // 0x60
        "[?]", // 0x61
        "[?]", // 0x62
        "[?]", // 0x63
        "[?]", // 0x64
        "[?]", // 0x65
        "[?]", // 0x66
        "[?]", // 0x67
        "[?]", // 0x68
        "[?]", // 0x69
        "", // 0x6a
        "", // 0x6b
        "", // 0x6c
        "", // 0x6d
        "", // 0x6e
        "", // 0x6f
        "0", // 0x70
        "", // 0x71
        "", // 0x72
        "", // 0x73
        "4", // 0x74
        "5", // 0x75
        "6", // 0x76
        "7", // 0x77
        "8", // 0x78
        "9", // 0x79
        "+", // 0x7a
        "-", // 0x7b
        "=", // 0x7c
        "(", // 0x7d
        ")", // 0x7e
        "n", // 0x7f
        "0", // 0x80
        "1", // 0x81
        "2", // 0x82
        "3", // 0x83
        "4", // 0x84
        "5", // 0x85
        "6", // 0x86
        "7", // 0x87
        "8", // 0x88
        "9", // 0x89
        "+", // 0x8a
        "-", // 0x8b
        "=", // 0x8c
        "(", // 0x8d
        ")", // 0x8e
        "[?]", // 0x8f
        "[?]", // 0x90
        "[?]", // 0x91
        "[?]", // 0x92
        "[?]", // 0x93
        "[?]", // 0x94
        "[?]", // 0x95
        "[?]", // 0x96
        "[?]", // 0x97
        "[?]", // 0x98
        "[?]", // 0x99
        "[?]", // 0x9a
        "[?]", // 0x9b
        "[?]", // 0x9c
        "[?]", // 0x9d
        "[?]", // 0x9e
        "[?]", // 0x9f
        "ECU", // 0xa0
        "CL", // 0xa1
        "Cr", // 0xa2
        "FF", // 0xa3
        "L", // 0xa4
        "mil", // 0xa5
        "N", // 0xa6
        "Pts", // 0xa7
        "Rs", // 0xa8
        "W", // 0xa9
        "NS", // 0xaa
        "D", // 0xab
        "EU", // 0xac
        "K", // 0xad
        "T", // 0xae
        "Dr", // 0xaf
        "Pf", // 0xb0
        "P", // 0xb1
        "G", // 0xb2
        "A", // 0xb3
        "C/", // 0xb4
        "[?]", // 0xb5
        "[?]", // 0xb6
        "[?]", // 0xb7
        "[?]", // 0xb8
        "[?]", // 0xb9
        "[?]", // 0xba
        "[?]", // 0xbb
        "[?]", // 0xbc
        "[?]", // 0xbd
        "[?]", // 0xbe
        "[?]", // 0xbf
        "[?]", // 0xc0
        "[?]", // 0xc1
        "[?]", // 0xc2
        "[?]", // 0xc3
        "[?]", // 0xc4
        "[?]", // 0xc5
        "[?]", // 0xc6
        "[?]", // 0xc7
        "[?]", // 0xc8
        "[?]", // 0xc9
        "[?]", // 0xca
        "[?]", // 0xcb
        "[?]", // 0xcc
        "[?]", // 0xcd
        "[?]", // 0xce
        "[?]", // 0xcf
        "", // 0xd0
        "", // 0xd1
        "", // 0xd2
        "", // 0xd3
        "", // 0xd4
        "", // 0xd5
        "", // 0xd6
        "", // 0xd7
        "", // 0xd8
        "", // 0xd9
        "", // 0xda
        "", // 0xdb
        "", // 0xdc
        "", // 0xdd
        "", // 0xde
        "", // 0xdf
        "", // 0xe0
        "", // 0xe1
        "", // 0xe2
        "", // 0xe3
        "[?]", // 0xe4
        "[?]", // 0xe5
        "[?]", // 0xe6
        "[?]", // 0xe7
        "[?]", // 0xe8
        "[?]", // 0xe9
        "[?]", // 0xea
        "[?]", // 0xeb
        "[?]", // 0xec
        "[?]", // 0xed
        "[?]", // 0xee
        "[?]", // 0xef
        "[?]", // 0xf0
        "[?]", // 0xf1
        "[?]", // 0xf2
        "[?]", // 0xf3
        "[?]", // 0xf4
        "[?]", // 0xf5
        "[?]", // 0xf6
        "[?]", // 0xf7
        "[?]", // 0xf8
        "[?]", // 0xf9
        "[?]", // 0xfa
        "[?]", // 0xfb
        "[?]", // 0xfc
        "[?]", // 0xfd
        "[?]" // 0xfe
    };
}
