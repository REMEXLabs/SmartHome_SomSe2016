<?php

namespace Application\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * CarerPatient
 *
 * @ORM\Table(name="carer_patient", indexes={@ORM\Index(name="fk_carer_cp_idx", columns={"carerId"}), @ORM\Index(name="fk_patient_cp_idx", columns={"patientId"})})
 * @ORM\Entity
 */
class CarerPatient
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
     * @var \DateTime
     *
     * @ORM\Column(name="datetime", type="datetime", nullable=false)
     */
    private $datetime;

    /**
     * @var \Application\Entity\Carer
     *
     * @ORM\ManyToOne(targetEntity="Application\Entity\Carer")
     * @ORM\JoinColumns({
     *   @ORM\JoinColumn(name="carerId", referencedColumnName="id")
     * })
     */
    private $carerid;

    /**
     * @var \Application\Entity\Patient
     *
     * @ORM\ManyToOne(targetEntity="Application\Entity\Patient")
     * @ORM\JoinColumns({
     *   @ORM\JoinColumn(name="patientId", referencedColumnName="id")
     * })
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
     * Set datetime
     *
     * @param \DateTime $datetime
     *
     * @return CarerPatient
     */
    public function setDatetime($datetime)
    {
        $this->datetime = $datetime;

        return $this;
    }

    /**
     * Get datetime
     *
     * @return \DateTime
     */
    public function getDatetime()
    {
        return $this->datetime;
    }

    /**
     * Set carerid
     *
     * @param \Application\Entity\Carer $carerid
     *
     * @return CarerPatient
     */
    public function setCarerid(\Application\Entity\Carer $carerid = null)
    {
        $this->carerid = $carerid;

        return $this;
    }

    /**
     * Get carerid
     *
     * @return \Application\Entity\Carer
     */
    public function getCarerid()
    {
        return $this->carerid;
    }

    /**
     * Set patientid
     *
     * @param \Application\Entity\Patient $patientid
     *
     * @return CarerPatient
     */
    public function setPatientid(\Application\Entity\Patient $patientid = null)
    {
        $this->patientid = $patientid;

        return $this;
    }

    /**
     * Get patientid
     *
     * @return \Application\Entity\Patient
     */
    public function getPatientid()
    {
        return $this->patientid;
    }
}
