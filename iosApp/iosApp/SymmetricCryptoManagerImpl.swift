//
//  SymmetricCryptoManagerImpl.swift
//  iosApp
//
//  Created by Samuele Bruschi on 13/04/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import Foundation
import ComposeApp
import CryptoKit
import Security
import CryptoSwift

public class SymmetricCryptoManagerImpl: SymmetricCryptoManager {
    
    public func decryptFromByteArrayToByteArray(key: String, bytes: KotlinByteArray) -> KotlinByteArray {
        do {
            // Convert KotlinByteArray to Swift [UInt8]
            let inputBytes = kotlinByteArrayToUInt8Array(bytes: bytes)

            // Parse the input bytes
            var index = 0

            // Extract IV size (1 byte)
            let ivSize = Int(inputBytes[index])
            index += 1

            // Extract IV bytes
            let iv = Array(inputBytes[index..<(index + ivSize)])
            index += ivSize

            // Extract ciphertext size (4 bytes, little-endian)
            let sizeBytes = Array(inputBytes[index..<(index + 4)])
            let ciphertextSize = Int(sizeBytes[0]) |
                                (Int(sizeBytes[1]) << 8) |
                                (Int(sizeBytes[2]) << 16) |
                                (Int(sizeBytes[3]) << 24)
            index += 4

            // Extract ciphertext bytes
            let ciphertext = Array(inputBytes[index..<(index + ciphertextSize)])

            // Get symmetric key from Keychain
            let key = try getOrCreateSymmetricKey(key: key)

            // In GCM mode, the tag is included in the ciphertext when using CryptoSwift
            // We need to extract it for decryption
            let actualCiphertext = Array(ciphertext[0..<(ciphertext.count - 16)])
            let tag = Array(ciphertext[(ciphertext.count - 16)..<ciphertext.count])

            // Decrypt data using AES/GCM
            let plaintext = try decrypt(ciphertext: actualCiphertext, nonce: iv, tag: tag, key: key)

            // Convert back to KotlinByteArray
            return uint8ArrayToKotlinByteArray(bytes: plaintext)
        } catch {
            // In case of error, throw an exception
            NSLog("Error in decryption: \(error)")
            fatalError("Decryption failed: \(error)")
        }
    }


    public func encryptFromByteArrayToByteArray(key: String, bytes: KotlinByteArray) -> KotlinByteArray {
        do {
            // Convert KotlinByteArray to Swift [UInt8]
            let plaintext = kotlinByteArrayToUInt8Array(bytes: bytes)

            // Get or create symmetric key from Keychain
            let key = try getOrCreateSymmetricKey(key: key)

            // Encrypt data using AES/GCM
            let (ciphertext, nonce, _) = try encrypt(plaintext: plaintext, key: key)

            // Create output byte array with format matching Android implementation
            var outputBytes = [UInt8]()

            // Add IV size (1 byte)
            outputBytes.append(UInt8(nonce.count))

            // Add IV bytes
            outputBytes.append(contentsOf: nonce)

            // Add ciphertext size (4 bytes, little-endian)
            var sizeBytes = [UInt8](repeating: 0, count: 4)
            sizeBytes[0] = UInt8(ciphertext.count & 0xFF)
            sizeBytes[1] = UInt8((ciphertext.count >> 8) & 0xFF)
            sizeBytes[2] = UInt8((ciphertext.count >> 16) & 0xFF)
            sizeBytes[3] = UInt8((ciphertext.count >> 24) & 0xFF)
            outputBytes.append(contentsOf: sizeBytes)

            // Add ciphertext bytes
            outputBytes.append(contentsOf: ciphertext)

            // Convert back to KotlinByteArray
            return uint8ArrayToKotlinByteArray(bytes: outputBytes)
        } catch {
            // In case of error, throw an exception
            NSLog("Error in encryption: \(error)")
            fatalError("Encryption failed: \(error)")
        }
    }

    func getOrCreateSymmetricKey(key: String) throws -> [UInt8] {
        let tag = key.data(using: .utf8)!

        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrAccount as String: key,
            kSecReturnData as String: true,
            kSecMatchLimit as String: kSecMatchLimitOne
        ]

        var item: CFTypeRef?
        let status = SecItemCopyMatching(query as CFDictionary, &item)

        if status == errSecSuccess, let keyData = item as? Data {
            return [UInt8](keyData)
        }

        // If key doesn't exist, generate a new one
        let key = AES.randomIV(32) // 256 bit key
        let keyData = Data(key)

        let addQuery: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrAccount as String: key,
            kSecValueData as String: keyData,
            kSecAttrAccessible as String: kSecAttrAccessibleWhenUnlocked
        ]

        let addStatus = SecItemAdd(addQuery as CFDictionary, nil)
        guard addStatus == errSecSuccess else {
            throw NSError(domain: "KeychainError", code: Int(addStatus), userInfo: nil)
        }

        return key
    }

    func encrypt(plaintext: [UInt8], key: [UInt8]) throws -> (cipher: [UInt8], nonce: [UInt8], tag: [UInt8]) {
        let nonce = AES.randomIV(12) // GCM standard nonce size

        let gcm = GCM(iv: nonce, mode: .combined)
        let aes = try AES(key: key, blockMode: gcm, padding: .noPadding)
        let encrypted = try aes.encrypt(plaintext)

        // In combined mode, the authentication tag is already appended to the ciphertext
        let tag = gcm.authenticationTag!

        return (encrypted, nonce, tag)
    }

    func decrypt(ciphertext: [UInt8], nonce: [UInt8], tag: [UInt8], key: [UInt8]) throws -> [UInt8] {
        let gcm = GCM(iv: nonce, authenticationTag: tag, additionalAuthenticatedData: nil, mode: .combined)
        let aes = try AES(key: key, blockMode: gcm, padding: .noPadding)
        return try aes.decrypt(ciphertext)
    }

    // Helper to convert KotlinByteArray to Swift [UInt8]
    func kotlinByteArrayToUInt8Array(bytes: KotlinByteArray) -> [UInt8] {
        var result = [UInt8]()
        let iterator = bytes.iterator()
        while iterator.hasNext() {
            result.append(UInt8(bitPattern: iterator.nextByte()))
        }
        return result
    }

    // Helper to convert Swift [UInt8] to KotlinByteArray
    func uint8ArrayToKotlinByteArray(bytes: [UInt8]) -> KotlinByteArray {
        let result = KotlinByteArray(size: Int32(bytes.count))
        for (index, byte) in bytes.enumerated() {
            result.set(index: Int32(index), value: Int8(bitPattern: byte))
        }
        return result
    }



}
