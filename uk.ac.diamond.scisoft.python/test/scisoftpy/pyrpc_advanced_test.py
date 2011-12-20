###
# Copyright © 2011 Diamond Light Source Ltd.
# Contact :  ScientificSoftware@diamond.ac.uk
# 
# This is free software: you can redistribute it and/or modify it under the
# terms of the GNU General Public License version 3 as published by the Free
# Software Foundation.
# 
# This software is distributed in the hope that it will be useful, but 
# WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
# Public License for more details.
# 
# You should have received a copy of the GNU General Public License along
# with this software. If not, see <http://www.gnu.org/licenses/>.
###

'''
Created on 1 May 2011

@author: Jonah
'''
import unittest
import scisoftpy.python.pyrpc as rpc
import thread
import threading

PORT = 8715

class Test(unittest.TestCase):

    def testMultipleHandlers(self):
        rpcserver = rpc.rpcserver(PORT)
        rpcserver.add_handler("cat", lambda s1, s2: s1 + s2)
        rpcserver.add_handler("len", lambda s1, s2: len(s1 + s2))
        
        thread.start_new_thread(rpcserver.serve_forever, ())
        try:
            rpcclient = rpc.rpcclient(PORT)
            result = rpcclient.cat("Hello, ", "World!")
            self.assertEqual("Hello, World!", result)
            result = rpcclient.len("Hello, ", "World!")
            self.assertEqual(len("Hello, World!"), result)
        finally:
            rpcserver.shutdown()
            rpcserver.close()

    def testExceptionOnHandlerFlattening(self):
        rpcserver = rpc.rpcserver(PORT)
        rpcserver.add_handler("flaterror", lambda o: object())
        
        thread.start_new_thread(rpcserver.serve_forever, ())
        try:
            rpcclient = rpc.rpcclient(PORT)
            self.assertRaises(Exception, rpcclient.flaterror, ("Hello",))
        finally:
            rpcserver.shutdown()
            rpcserver.close()

    def testSingleIntegerArg(self):
        rpcserver = rpc.rpcserver(PORT)
        rpcserver.add_handler("echo", lambda o: o)
        
        thread.start_new_thread(rpcserver.serve_forever, ())
        try:
            rpcclient = rpc.rpcclient(PORT)
            self.assertRaises(Exception, rpcclient.flaterror, (18,))
        finally:
            rpcserver.shutdown()
            rpcserver.close()

    def testConnectionTimesOutQuicklyEnough(self):
        rpcclient = rpc.rpcclient(PORT)
        t = threading.Thread(target=self.assertRaises, args=(Exception, rpcclient.doesnotexist, ("Hello",)))
        t.start()
        t.join(2.0)
        self.assertFalse(t.isAlive())
        


if __name__ == "__main__":
    #import sys;sys.argv = ['', 'Test.testName']
    unittest.main()
