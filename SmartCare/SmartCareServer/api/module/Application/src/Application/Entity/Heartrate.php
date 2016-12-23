<?php

namespace Application\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * Heartrate
 *
 * @ORM\Table(name="heartrate", indexes={@ORM\Index(name="fk_patient_heartrate_idx", columns={"patientId"})})
 * @ORM\Entity
 */
class Heartrate
{
    /**
     * @var integer
     *
     * @ORM\Column(name="id", type="integer", nullable=false)
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="IDENTITY")
     */
    private $id;

    /**
     * @var integer
     *
     * @ORM\Column(name="value", type="integer", nullable=false)
     */
    private $value;

    /**
     * @var \DateTime
     *
     * @ORM\Column(name="date", type="datetime", nullable=false)
     */
    private $date;

    /**
     * @var integer
     *
     * @ORM\Column(name="patientId", type="integer", nullable=false)
     */
    private $patientid;



    /**
     * Get id
     *
     * @return integer
     */
    public function getId()
    {
        return $this->id;
    }

    /**
     * Set value
     *
     * @param integer $value
     *
     * @return Heartrate
     */
    public function setValue($value)
    {
        $this->value = $value;

        return $this;
    }

    /**
     * Get value
     *
     * @return integer
     */
    public function getValue()
    {
        return $this->value;
    }

    /**
     * Set date
     *
     * @param \DateTime $date
     *
     * @return Heartrate
     */
    public function setDate($date)
    {
        $this->date = $date;

        return $this;
    }

    /**
     * Get date
     *
     * @return \DateTime
     */
    public function getDate()
    {
        return $this->date;
    }

    /**
     * Set patientid
     *
     * @param integer $patientid
     *
     * @return Heartrate
     */
    public function setPatientid($patientid)
    {
        $this->patientid = $patientid;

        return $this;
    }

    /**
     * Get patientid
     *
     * @return integer
     */
    public function getPatientid()
    {
        return $this->patientid;
    }
}
