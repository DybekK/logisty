<?php

namespace Core\Domain\Value;

/**
 * @template I
 * @template O
 */
interface ValueObject
{
    /**
     * @template IS
     * @template OS
     *
     * @param IS $value
     * @return OS
     */
    public static function of($value);

    /**
     * @param O $object
     */
    public function eq($object): bool;
}
