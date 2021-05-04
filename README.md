# dynamo-scan
[![Build](https://github.com/gregwhitaker/dynamo-scan/actions/workflows/gradle-build.yml/badge.svg)](https://github.com/gregwhitaker/dynamo-scan/actions/workflows/gradle-build.yml)

Library for scanning records in AWS [DynamoDB](https://aws.amazon.com/dynamodb/) tables.

Dynamo-scan makes it easy to setup a parallel scan of records within a DynamoDB table and receive the records as a stream of
items using [Project Reactor](https://projectreactor.io/).

## Examples
Please see the included [example project](dynamo-scan-example) for a demonstration on configuring and running dynamo-scan.

## Bugs and Feedback
For bugs, questions, and discussions please use the [Github Issues](https://github.com/gregwhitaker/dynamo-scan/issues).

## License
MIT License

Copyright (c) 2021 Greg Whitaker

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
